package fr.gstraymond.biz

import com.magic.card.search.commons.log.Log
import fr.gstraymond.android.prefs
import fr.gstraymond.models.search.response.Rule
import fr.gstraymond.network.ElasticSearchService

class RulesFetcher(private val elasticSearchService: ElasticSearchService,
                   private val save: (List<Rule>) -> Unit) {

    private val log = Log(javaClass)

    fun fetch() {
        elasticSearchService.getMtgRules("version")?.elem?._source?.filename?.let { filename ->
            log.d("rules version: local:[${prefs.rulesVersion}] remote:[$filename]")
            if (filename != prefs.rulesVersion) {
                val allRules = elasticSearchService.getMtgRules("rules")
                log.d("getMtgRules found: ${allRules?.elem?._source?.rules?.size}")
                allRules?.run {
                    save(elem._source.rules ?: listOf())
                    prefs.rulesVersion = filename
                }
            }
        }
    }
}