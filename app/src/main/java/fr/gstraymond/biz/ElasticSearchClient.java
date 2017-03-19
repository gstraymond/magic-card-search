package fr.gstraymond.biz;

import android.text.TextUtils;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.gstraymond.db.json.HistoryList;
import fr.gstraymond.models.search.request.Request;
import fr.gstraymond.models.search.response.SearchResult;
import fr.gstraymond.network.ElasticSearchService;
import fr.gstraymond.network.Result;

public class ElasticSearchClient {

    private HistoryList historyDataSource;
    private ElasticSearchService elasticSearchService;
    private MapperUtil<Request> mapperUtil;

    private Log log = new Log(this);

    public ElasticSearchClient(ElasticSearchService elasticSearchService,
                               HistoryList historyList,
                               MapperUtil<Request> mapperUtil) {
        this.historyDataSource = historyList;
        this.elasticSearchService = elasticSearchService;
        this.mapperUtil = mapperUtil;
    }

    SearchResult process(SearchOptions options) {
        log.d("options as json : %s", options);
        Request request = Request.Companion.fromOptions(options);
        String queryAsJson = mapperUtil.asJsonString(request);

        if (options.getAddToHistory()) {
            log.i("add to history : " + options);
            historyDataSource.appendHistory(options);
        }

        Result<SearchResult> response = elasticSearchService.search(queryAsJson);
        if (response == null) return null;

        SearchResult searchResult = response.getElem();

        Answers.getInstance().logSearch(buildSearchEvent(options, searchResult.getHits().getTotal()));

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
