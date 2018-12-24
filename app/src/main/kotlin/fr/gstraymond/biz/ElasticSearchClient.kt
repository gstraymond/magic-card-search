package fr.gstraymond.biz

import com.magic.card.search.commons.json.MapperUtil
import com.magic.card.search.commons.log.Log
import fr.gstraymond.db.json.HistoryList
import fr.gstraymond.models.search.request.Request
import fr.gstraymond.models.search.response.SearchResult
import fr.gstraymond.network.ElasticSearchService

class ElasticSearchClient(private val elasticSearchService: ElasticSearchService,
                          private val historyDataSource: HistoryList,
                          private val mapperUtil: MapperUtil<Request>) {

    private val log = Log(this)

    internal fun process(options: SearchOptions): SearchResult? {
        log.d("options as json : %s", options)
        val queryAsJson = mapperUtil.asJsonString(Request.fromOptions(options))

        if (options.addToHistory) {
            log.i("add to history : $options")
            historyDataSource.appendHistory(options)
        }

        return elasticSearchService.search(queryAsJson)?.elem
    }
}
