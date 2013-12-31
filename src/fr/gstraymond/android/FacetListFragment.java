package fr.gstraymond.android;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import fr.gstraymond.R;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SearchProcessor;
import fr.gstraymond.magicsearch.model.response.SearchResult;
import fr.gstraymond.magicsearch.model.response.facet.Term;
import fr.gstraymond.ui.FacetListAdapter;

public class FacetListFragment extends ListFragment {

	private SearchResult result;
	private SearchOptions options;
	private FacetListAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		adapter = new FacetListAdapter(result.getFacets(), options);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		Term term = adapter.getTerm(position);
		if (term.getCount() > -1) {
			String facet = adapter.getFacet(term);

			if (adapter.isTermSelected(term)) {
				options.removeFacet(facet, term.getTerm());
			} else {
				options.addFacet(facet, term.getTerm());
			}
			options.setAppend(false);
			options.setFrom(0);

			MagicCardListActivity activity = (MagicCardListActivity) getActivity();

			new SearchProcessor(activity, options, R.string.loading_facet)
					.execute();
		}
	}

	public void setResult(SearchResult result) {
		this.result = result;
	}

	public void setOptions(SearchOptions options) {
		this.options = options;
	}
}
