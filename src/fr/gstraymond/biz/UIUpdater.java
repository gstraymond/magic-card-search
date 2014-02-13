package fr.gstraymond.biz;

import static fr.gstraymond.constants.Consts.CARD_LIST;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gstraymond.R;
import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.android.CardListFragment;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.search.model.response.Hit;
import fr.gstraymond.search.model.response.SearchResult;
import fr.gstraymond.search.model.response.facet.Term;
import fr.gstraymond.tools.MapperUtil;
import fr.gstraymond.ui.FacetListAdapter;

public class UIUpdater extends AsyncTask<Void, Void, SearchResult> {
	
	private CardListActivity activity;
	private String resultAsString;
	private MapperUtil<SearchResult> mapperUtil;

	public UIUpdater(CardListActivity activity, String resultAsString, ObjectMapper objectMapper) {
		this(activity);
		this.resultAsString = resultAsString;
		this.mapperUtil = new MapperUtil<SearchResult>(objectMapper, SearchResult.class);
	}

	public UIUpdater(CardListActivity activity) {
		this.activity = activity;
	}

	@Override
	protected SearchResult doInBackground(Void... params) {
		return mapperUtil.read(resultAsString);
	}

	@Override
	protected void onPostExecute(SearchResult result) {
		if (result == null) {
			getWelcomeTextView().setText(R.string.failed_search);
			return;
		}
		
		int totalCardCount = 0;
		ArrayList<Card> cards = new ArrayList<Card>();
		
		if (result.getHits() != null) {
			totalCardCount = result.getHits().getTotal();
			for (Hit hit : result.getHits().getHits()) {
				cards.add(hit.get_source());
			}
		}

		int textId = R.string.progress_cards_found;
		if (totalCardCount <= 1) {
			textId = R.string.progress_card_found;
		}

		getWelcomeTextView().setText(totalCardCount + " " + activity.getString(textId));

		updateUIList(totalCardCount, cards);
		updateUIFacets(result);
	}

	private void updateUIList(int totalCardCount, ArrayList<Card> cards) {
		if (getOptions().isAppend()) {
			CardListFragment fragment = getCardListFragment();
			fragment.appendCards(cards);
		} else {
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList(CARD_LIST, cards);
			CardListFragment fragment = new CardListFragment();
			fragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.card_list, fragment).commit();
			
		}
		activity.setTotalCardCount(totalCardCount);
	}


	private void updateUIFacets(SearchResult result) {
		if (! getOptions().isAppend()) {
			final FacetListAdapter facetListAdapter = new FacetListAdapter(result.getFacets(), getOptions());
			getFacetListView().setAdapter(facetListAdapter);
			// TODO : extract to separate class
			getFacetListView().setOnChildClickListener(new OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView parent, View view,
						int groupPosition, int childPosition, long id) {
					Term term = (Term) facetListAdapter.getChild(groupPosition, childPosition);
					if (term.getCount() > -1) {
						String facet = facetListAdapter.getFacet(term);

						if (facetListAdapter.isTermSelected(term)) {
							getOptions().removeFacet(facet, term.getTerm());
						} else {
							getOptions().addFacet(facet, term.getTerm());
						}
						getOptions().setAppend(false);
						getOptions().setFrom(0);
						new SearchProcessor(activity, getOptions(), R.string.loading_facet).execute();
					}
					return true;
				}
			});
		}
	}

	private TextView getWelcomeTextView() {
		return (TextView) activity.findViewById(R.id.welcome_text_view);
	}

	private SearchOptions getOptions() {
		return activity.getCurrentSearch();
	}

	private FragmentManager getFragmentManager() {
		return activity.getFragmentManager();
	}

	private CardListFragment getCardListFragment() {
		Fragment fragment = getFragmentManager().findFragmentById(R.id.card_list);
		return (CardListFragment) fragment; 
	}

	private ExpandableListView getFacetListView() {
		return (ExpandableListView) activity.findViewById(R.id.left_drawer);
	}
}
