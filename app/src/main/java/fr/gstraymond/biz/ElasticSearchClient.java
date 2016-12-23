package fr.gstraymond.biz;

import android.text.TextUtils;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.gstraymond.db.json.JsonHistoryDataSource;
import fr.gstraymond.models.search.request.Request;
import fr.gstraymond.models.search.response.SearchResult;
import fr.gstraymond.network.ElasticSearchConnector;
import fr.gstraymond.network.ElasticSearchService;
import fr.gstraymond.network.Result;
import retrofit2.Response;

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

        if (options.isAddToHistory()) {
            log.i("add to history : " + options);
            historyDataSource.appendHistory(options);
        }

        try {
            Response<SearchResult> response = elasticSearchService.search(queryAsJson).execute();
            if (response.code() != 200) {
                log.w("process: bad response %s %s", response.code(), queryAsJson);
                return null;
            }
            SearchResult searchResult = response.body();
            callbacks.getResponse();

            callbacks.end();
            Answers.getInstance().logSearch(buildSearchEvent(options, searchResult.getHits().getTotal()));

            return searchResult;
        } catch (IOException e) {
            log.e("process: " + e.getMessage(), e);
            return null;
        }
    }

    private SearchEvent buildSearchEvent(SearchOptions options, int results) {
        SearchEvent searchEvent = new SearchEvent()
                .putQuery(options.getQuery())
                .putCustomAttribute("okGoogle", options.isFromOkGoogle() + "")
                .putCustomAttribute("results", results);

        if (options.getFacets().isEmpty())
            return searchEvent;

        List<String> facets = new ArrayList<>(options.getFacets().keySet());
        Collections.sort(facets);
        return searchEvent
                .putCustomAttribute("facets", TextUtils.join(" - ", facets));
    }
}
