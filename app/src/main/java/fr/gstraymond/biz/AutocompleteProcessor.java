package fr.gstraymond.biz;

import android.os.AsyncTask;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;
import com.squareup.moshi.Moshi;

import java.util.List;

import fr.gstraymond.models.autocomplete.request.AutocompleteRequest;
import fr.gstraymond.models.autocomplete.response.AutocompleteResult;
import fr.gstraymond.models.autocomplete.response.Option;
import fr.gstraymond.network.ElasticSearchService;
import fr.gstraymond.network.Result;

public class AutocompleteProcessor extends AsyncTask<String, String, AutocompleteResult> {

    private ElasticSearchService searchService;
    private Callbacks callbacks;
    private MapperUtil<AutocompleteRequest> mapperUtil;

    private Log log = new Log(this);

    public AutocompleteProcessor(Moshi objectMapper, ElasticSearchService searchService, Callbacks callbacks) {
        this.searchService = searchService;
        this.callbacks = callbacks;
        this.mapperUtil = MapperUtil.fromType(objectMapper, AutocompleteRequest.class);
    }

    @Override
    protected AutocompleteResult doInBackground(String... strings) {
        String query = strings[0];
        String q = mapperUtil.asJsonString(AutocompleteRequest.Companion.withQuery(query));
        Result<AutocompleteResult> result = searchService.autocomplete(q);
        if (result == null) return AutocompleteResult.Companion.empty();

        CustomEvent event = new CustomEvent("autocomplete")
                .putCustomAttribute("results", result.getElem().getResults().size())
                .putCustomAttribute("http duration", result.getHttpDuration());
        if (query.length() > 2) event.putCustomAttribute("query", query);
        Answers.getInstance().logCustom(event);

        return result.getElem();
    }

    @Override
    protected void onPostExecute(AutocompleteResult autocompleteResult) {
        List<Option> results = autocompleteResult.getResults();
        log.d("autocomplete %s elems", results.size());
        callbacks.bindAutocompleteResults(results);
    }

    public interface Callbacks {
        void bindAutocompleteResults(List<Option> results);
    }
}
