package fr.gstraymond.android

import com.magic.card.search.commons.application.BaseApplication
import com.magic.card.search.commons.json.MapperUtil
import com.magic.card.search.commons.log.Log
import fr.gstraymond.biz.DeckManager
import fr.gstraymond.biz.ElasticSearchClient
import fr.gstraymond.db.json.*
import fr.gstraymond.impex.DeckResolver
import fr.gstraymond.models.search.request.Request
import fr.gstraymond.network.ElasticSearchApi
import fr.gstraymond.network.ElasticSearchService
import fr.gstraymond.network.RetrofitBuilder.buildRetrofit
import java.util.*

val prefs: Prefs by lazy {
    CustomApplication.prefs!!
}

class CustomApplication : BaseApplication() {

    companion object {
        var prefs: Prefs? = null
    }

    val elasticSearchClient by lazy {
        ElasticSearchClient(
                searchService,
                historyList,
                MapperUtil.fromType(objectMapper, Request::class.java))
    }
    val searchService by lazy {
        val elasticSearchApi = buildRetrofit(objectMapper, this).create(ElasticSearchApi::class.java)
        ElasticSearchService(elasticSearchApi)
    }

    val historyList by lazy { HistoryList(this, objectMapper) }
    val wishList by lazy { WishList(this, objectMapper) }
    val deckList by lazy { DeckList(this, objectMapper) }
    val cardListBuilder by lazy { DeckCardListBuilder(this, objectMapper, deckList) }
    val deckResolver by lazy { DeckResolver(searchService) }
    val deckManager by lazy { DeckManager(deckList, cardListBuilder) }

    var listsCardId: Map<String, List<String>> = mapOf()

    private val log = Log(javaClass)

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
        refreshLists()
        historyList.migrate()
        wishList.migrate()
        registerActivityLifecycleCallbacks(LogActivityLifecycleCallbacks())

        // migrate
        CardListMigrator.migrate(this, objectMapper, deckList)
    }

    fun refreshLists() {
        log.d("refreshLists...")
        val now = Date().time
        // FIXME performance problem
        /*listsCardId =
                deckList.flatMap { deck ->
                            cardListBuilder
                                    .build(deck.id)
                                    .map { it.card.getId() to deck.id.toString() }
                        }
                        .plus(wishList.map { it.getId() to "wishlist" })
                        .groupBy { it.first }
                        .mapValues { it.value.map { it.second } } */
        log.d("refreshLists: ${Date().time - now}ms")
    }
}
