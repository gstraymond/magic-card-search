package fr.gstraymond.biz;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;

import java.util.List;

import fr.gstraymond.autocomplete.response.AutocompleteResult;
import fr.gstraymond.network.ElasticSearchConnector;
import fr.gstraymond.network.Result;

public class AutocompleteProcessor extends AsyncTask<String, String, AutocompleteResult> {

    private ElasticSearchConnector<AutocompleteResult> connector;
    private Callbacks callbacks;

    private static final String query = ("" +
            "{" +
            "  \"query\": {" +
            "    \"match_all\": {}" +
            "  }," +
            "  \"size\": 0," +
            "  \"suggest\": {" +
            "    \"card\": {" +
            "      \"text\": \"%s\"," +
            "      \"completion\": {" +
            "        \"size\": 10," +
            "        \"field\": \"suggest\"" +
            "      }" +
            "    }" +
            "  }" +
            "}").replace(" ", "");

    private Log log = new Log(this);

    public AutocompleteProcessor(ObjectMapper objectMapper, Context context, Callbacks callbacks) {
        MapperUtil<AutocompleteResult> mapperUtil = MapperUtil.fromType(objectMapper, AutocompleteResult.class);
        this.connector = new ElasticSearchConnector<>(context, mapperUtil);
        this.callbacks = callbacks;
    }

    @Override
    protected AutocompleteResult doInBackground(String... strings) {
        String q = String.format(query, strings[0]);
        Result<AutocompleteResult> result = connector.connect("autocomplete/card/_search", q);
        if (result == null) return new AutocompleteResult();
        return result.elem;
    }

    @Override
    protected void onPostExecute(AutocompleteResult autocompleteResult) {
        List<String> results = autocompleteResult.getResults();
        log.d("autocomplete %s", TextUtils.join(",", results));
        callbacks.bindAutocompleteResults(results);
    }

    public interface Callbacks {
        void bindAutocompleteResults(List<String> results);
    }
}
