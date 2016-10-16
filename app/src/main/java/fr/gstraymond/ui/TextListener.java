package fr.gstraymond.ui;

import android.widget.SearchView.OnQueryTextListener;

import com.magic.card.search.commons.log.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.gstraymond.R;
import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.autocomplete.response.Option;
import fr.gstraymond.biz.AutocompleteProcessor;
import fr.gstraymond.biz.Facets;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SearchProcessor;

import static fr.gstraymond.biz.AutocompleteProcessor.*;

public class TextListener implements OnQueryTextListener {

    private CardListActivity activity;
    private Callbacks callbacks;

    private boolean canSearch = true;

    private Log log = new Log(this);

    public TextListener(CardListActivity activity, Callbacks callbacks) {
        this.activity = activity;
        this.callbacks = callbacks;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        log.d("text: %s", text);
        if (text.isEmpty() || text.endsWith("\u00A0")) {
            callbacks.bindAutocompleteResults(new ArrayList<Option>());
            return false;
        }

        String query = text;
        if (text.contains("\u00A0")) {
            List<String> split = Arrays.asList(text.split("\u00A0"));
            query = split.get(split.size() - 1);
        }

        new AutocompleteProcessor(activity.getObjectMapper(), activity, callbacks).execute(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String text) {
        if (canSearch) {
            Facets facets = activity.getCurrentSearch().getFacets();
            SearchOptions options = new SearchOptions().setQuery(text.replace(":", "")).setFacets(facets);
            new SearchProcessor(activity, options, R.string.loading_initial).execute();
        }
        return true;
    }

    public void setCanSearch(boolean canSearch) {
        this.canSearch = canSearch;
    }

}
