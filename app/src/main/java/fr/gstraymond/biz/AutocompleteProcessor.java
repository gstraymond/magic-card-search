package fr.gstraymond.biz;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;

import fr.gstraymond.autocomplete.response.AutocompleteResult;
import fr.gstraymond.network.ElasticSearchConnector;
import fr.gstraymond.network.Result;

public class AutocompleteProcessor extends AsyncTask<String, String, AutocompleteResult> {

    private ElasticSearchConnector<AutocompleteResult> connector;

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
            "        \"field\": \"suggest\"" +
            "      }" +
            "    }" +
            "  }" +
            "}").replaceAll(" ", "");

    private Log log = new Log(this);

    public AutocompleteProcessor(ObjectMapper objectMapper, Context context) {
        MapperUtil<AutocompleteResult> mapperUtil = MapperUtil.fromType(objectMapper, AutocompleteResult.class);
        this.connector = new ElasticSearchConnector<>(context, mapperUtil);
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
        log.d("autocomplete %s", TextUtils.join(",", autocompleteResult.getResults()));

    }
}
