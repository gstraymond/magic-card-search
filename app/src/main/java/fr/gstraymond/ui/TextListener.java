package fr.gstraymond.ui;

import android.widget.SearchView.OnQueryTextListener;

import com.magic.card.search.commons.log.Log;

import fr.gstraymond.R;
import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.biz.AutocompleteProcessor;
import fr.gstraymond.biz.Facets;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SearchProcessor;

public class TextListener implements OnQueryTextListener {

    private CardListActivity activity;
    private boolean canSearch = true;

    private Log log = new Log(this);

    public TextListener(CardListActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        log.d("text: %s", text);
        new AutocompleteProcessor(activity.getObjectMapper(), activity).execute(text);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String text) {
        if (canSearch) {
            Facets facets = activity.getCurrentSearch().getFacets();
            SearchOptions options = new SearchOptions().setQuery(text).setFacets(facets);
            new SearchProcessor(activity, options, R.string.loading_initial).execute();
        }
        return true;
    }

    public void setCanSearch(boolean canSearch) {
        this.canSearch = canSearch;
    }

}
