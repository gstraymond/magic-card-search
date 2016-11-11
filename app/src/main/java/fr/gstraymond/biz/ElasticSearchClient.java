package fr.gstraymond.biz;

import android.text.TextUtils;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
import com.magic.card.search.commons.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.gstraymond.db.json.JsonHistoryDataSource;
import fr.gstraymond.network.ElasticSearchConnector;
import fr.gstraymond.network.Result;
import fr.gstraymond.search.model.request.Request;
import fr.gstraymond.search.model.response.SearchResult;

public class ElasticSearchClient {

    private JsonHistoryDataSource historyDataSource;
    private ElasticSearchConnector<SearchResult> connector;

    private Log log = new Log(this);

    public ElasticSearchClient(ElasticSearchConnector<SearchResult> connector, JsonHistoryDataSource jsonHistoryDataSource) {
        this.historyDataSource = jsonHistoryDataSource;
        this.connector = connector;
    }

    interface CallBacks {
        void start();

        void buildRequest();

        void getResponse();

        void end();
    }

    SearchResult process(SearchOptions options, CallBacks callbacks) {
        callbacks.start();
        log.d("options as json : %s", options);
        Request request = new Request(options);
        String queryAsJson = connector.getMapperUtil().asJsonString(request);
        callbacks.buildRequest();

        if (options.isAddToHistory()) {
            log.i("add to history : " + options);
            historyDataSource.appendHistory(options);
        }

        Result<SearchResult> result = connector.connect("magic/card/_search", "source", queryAsJson);
        callbacks.getResponse();

        if (result == null || result.elem == null) return null;
        SearchResult searchResult = result.elem;

        callbacks.end();
        Answers.getInstance().logSearch(buildSearchEvent(options, result.httpDuration, result.parseDuration, searchResult.getHits().getTotal()));

        return searchResult;
    }

    private SearchEvent buildSearchEvent(SearchOptions options, long httpDuration, long parseDuration, int results) {
        SearchEvent searchEvent = new SearchEvent()
                .putQuery(options.getQuery())
                .putCustomAttribute("okGoogle", options.isFromOkGoogle() + "")
                .putCustomAttribute("http duration", httpDuration)
                .putCustomAttribute("parse duration", parseDuration)
                .putCustomAttribute("results", results);

        if (options.getFacets().isEmpty())
            return searchEvent;

        List<String> facets = new ArrayList<>(options.getFacets().keySet());
        Collections.sort(facets);
        return searchEvent
                .putCustomAttribute("facets", TextUtils.join(" - ", facets));
    }
}
