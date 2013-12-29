package fr.gstraymond.biz;

import java.io.IOException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.gstraymond.R;
import fr.gstraymond.android.MagicCardListActivity;
import fr.gstraymond.android.MagicCardListFragment;
import fr.gstraymond.magicsearch.model.response.Hit;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.magicsearch.model.response.SearchResult;
import fr.gstraymond.magicsearch.model.response.facet.Term;
import fr.gstraymond.ui.FacetListAdapter;

public class UIUpdater extends AsyncTask<Void, Void, SearchResult> {
	
	private MagicCardListActivity activity;
	private String resultAsString;

	public UIUpdater(MagicCardListActivity activity, String resultAsString) {
		this(activity);
		this.resultAsString = resultAsString;
	}

	public UIUpdater(MagicCardListActivity activity) {
		this.activity = activity;
	}

	@Override
	protected SearchResult doInBackground(Void... params) {
		return getResult();
	}

	private SearchResult getResult() {
		try {
			return activity.getObjectMapper().readValue(resultAsString, SearchResult.class);
		} catch (JsonParseException e) {
			Log.e(getClass().getName(), "parse", e);
		} catch (JsonMappingException e) {
			Log.e(getClass().getName(), "parse", e);
		} catch (IOException e) {
			Log.e(getClass().getName(), "parse", e);
		}
		return null;
	}


	@Override
	protected void onPostExecute(SearchResult result) {
		int totalCardCount = 0;
		ArrayList<MagicCard> cards = new ArrayList<MagicCard>();

		if (result.getHits() != null) {
			totalCardCount = result.getHits().getTotal();
			for (Hit hit : result.getHits().getHits()) {
				cards.add(hit.get_source());
			}
		}
		
		String text = totalCardCount + " ";
		if (totalCardCount > 1) {
			text += activity.getString(R.string.progress_cards_found);
		} else {
			text += activity.getString(R.string.progress_card_found);
		}
		
		getWelcomeTextView().setText(text);
		
		updateUIList(totalCardCount, cards);
		updateUIFacets(result);
	}

	private void updateUIList(int totalCardCount, ArrayList<MagicCard> cards) {
		if (getOptions().isAppend()) {
			MagicCardListFragment fragment = getMagicCardListFragment();
			fragment.appendCards(cards);
		} else {
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList(MagicCardListFragment.CARDS, cards);
			bundle.putInt(MagicCardListFragment.TOTAL_CARD_COUNT, totalCardCount);
			Fragment fragment = new MagicCardListFragment();
			fragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.magiccard_list, fragment).commit();
		}
	}

	private void updateUIFacets(SearchResult result) {
		if (! getOptions().isAppend()) {
			final FacetListAdapter facetListAdapter = new FacetListAdapter(result.getFacets(), getOptions());
			getFacetListView().setAdapter(facetListAdapter);
			getFacetListView().setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					Term term = facetListAdapter.getTerm(position);
					if (term.getCount() > -1) {
						String facet = facetListAdapter.getFacet(term);

						if (facetListAdapter.isTermSelected(term)) {
							getOptions().removeFacet(facet, term.getTerm());
						} else {
							getOptions().addFacet(facet, term.getTerm());
						}
						getOptions().setAppend(false);
						getOptions().setFrom(0);
						new SearchProcessor(activity, getOptions()).execute();
					}
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
		return activity.getSupportFragmentManager();
	}

	private MagicCardListFragment getMagicCardListFragment() {
		Fragment fragment = getFragmentManager().findFragmentById(R.id.magiccard_list);
		return (MagicCardListFragment) fragment; 
	}

	private ListView getFacetListView() {
		return (ListView) activity.findViewById(R.id.facet_list);
	}
}
