package fr.gstraymond.biz;

import android.content.Context;
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
import fr.gstraymond.network.ElasticSearchConnector;
import fr.gstraymond.network.Result;
import fr.gstraymond.tools.VersionUtils;

public class AutocompleteProcessor extends AsyncTask<String, String, AutocompleteResult> {

    private ElasticSearchConnector<AutocompleteResult> connector;
    private Callbacks callbacks;
    private MapperUtil<AutocompleteRequest> mapperUtil;

    private Log log = new Log(this);

    public AutocompleteProcessor(Moshi objectMapper, Context context, Callbacks callbacks) {
        MapperUtil<AutocompleteResult> mapperUtil = MapperUtil.fromType(objectMapper, AutocompleteResult.class);
        this.connector = new ElasticSearchConnector<>(VersionUtils.getAppName(context), mapperUtil);
        this.callbacks = callbacks;
        this.mapperUtil = MapperUtil.fromType(objectMapper, AutocompleteRequest.class);
    }

    @Override
    protected AutocompleteResult doInBackground(String... strings) {
        String query = strings[0];
        String q = mapperUtil.asJsonString(AutocompleteRequest.Companion.withQuery(query));
        Result<AutocompleteResult> result = connector.connect("autocomplete/card/_search", "source", q);
        if (result == null) return AutocompleteResult.Companion.empty();

        CustomEvent event = new CustomEvent("autocomplete")
                .putCustomAttribute("results", result.elem.getResults().size())
                .putCustomAttribute("http duration", result.httpDuration)
                .putCustomAttribute("parse duration", result.parseDuration);
        if (query.length() > 2) event.putCustomAttribute("query", query);
        Answers.getInstance().logCustom(event);

        return result.elem;
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
