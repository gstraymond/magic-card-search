package fr.gstraymond.android

import com.magic.card.search.commons.application.BaseApplication
import com.magic.card.search.commons.json.MapperUtil
import fr.gstraymond.biz.DeckManager
import fr.gstraymond.biz.ElasticSearchClient
import fr.gstraymond.biz.RulesFetcher
import fr.gstraymond.biz.WishlistManager
import fr.gstraymond.db.json.*
import fr.gstraymond.impex.DeckResolver
import fr.gstraymond.models.search.request.Request
import fr.gstraymond.network.ElasticSearchApi
import fr.gstraymond.network.ElasticSearchService
import fr.gstraymond.network.RetrofitBuilder.buildRetrofit
import kotlin.concurrent.thread

val prefs: Prefs by lazy {
    CustomApplication.prefs!!
}

class CustomApplication : BaseApplication() {

    companion object {
        var prefs: Prefs? = null
    }

    val searchService by lazy {
        val elasticSearchApi = buildRetrofit(objectMapper, this).create(ElasticSearchApi::class.java)
        ElasticSearchService(elasticSearchApi)
    }

    val elasticSearchClient by lazy {
        ElasticSearchClient(
                searchService,
                historyList,
                MapperUtil.fromType(objectMapper, Request::class.java))
    }

    val historyList by lazy { HistoryList(this, objectMapper) }
    val wishList by lazy { WishList(this, objectMapper) }
    val deckList by lazy { DeckList(this, objectMapper) }
    val ruleList by lazy { RuleList(this, objectMapper) }
    val cardListBuilder by lazy { DeckCardListBuilder(this, objectMapper, deckList) }
    val deckResolver by lazy { DeckResolver(searchService) }
    val deckManager by lazy { DeckManager(deckList, cardListBuilder) }
    val wishlistManager by lazy { WishlistManager(wishList) }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
        registerActivityLifecycleCallbacks(LogActivityLifecycleCallbacks())

        fetchRules()
        // migrate
        CardListMigrator.migrate(this, objectMapper, deckList)
    }

    private fun fetchRules() {
        thread {
            RulesFetcher(searchService) {
                ruleList.clear()
                ruleList.save(it)
                ruleList.setLoaded()
            }.fetch()
        }
    }
}
