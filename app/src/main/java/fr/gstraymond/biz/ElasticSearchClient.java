package fr.gstraymond.biz;

import android.text.TextUtils;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.gstraymond.db.json.JsonHistoryDataSource;
import fr.gstraymond.models.search.request.Request;
import fr.gstraymond.models.search.response.SearchResult;
import fr.gstraymond.network.ElasticSearchService;
import fr.gstraymond.network.Result;

public class ElasticSearchClient {

    private JsonHistoryDataSource historyDataSource;
    private ElasticSearchService elasticSearchService;
    private MapperUtil<Request> mapperUtil;

    private Log log = new Log(this);

    public ElasticSearchClient(ElasticSearchService elasticSearchService,
                               JsonHistoryDataSource jsonHistoryDataSource,
                               MapperUtil<Request> mapperUtil) {
        this.historyDataSource = jsonHistoryDataSource;
        this.elasticSearchService = elasticSearchService;
        this.mapperUtil = mapperUtil;
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
        Request request = Request.Companion.fromOptions(options);
        String queryAsJson = mapperUtil.asJsonString(request);
        callbacks.buildRequest();

        if (options.getAddToHistory()) {
            log.i("add to history : " + options);
            historyDataSource.appendHistory(options);
        }

        Result<SearchResult> response = elasticSearchService.search(queryAsJson);
        if (response == null) return null;

        SearchResult searchResult = response.getElem();
        callbacks.getResponse();

        Answers.getInstance().logSearch(buildSearchEvent(options, searchResult.getHits().getTotal()));

        callbacks.end();
        return searchResult;
    }

    private SearchEvent buildSearchEvent(SearchOptions options, int results) {
        SearchEvent searchEvent = new SearchEvent()
                .putQuery(options.getQuery())
                .putCustomAttribute("results", results);

        if (options.getFacets().isEmpty())
            return searchEvent;

        List<String> facets = new ArrayList<>(options.getFacets().keySet());
        Collections.sort(facets);
        return searchEvent
                .putCustomAttribute("facets", TextUtils.join(" - ", facets));
    }
}
