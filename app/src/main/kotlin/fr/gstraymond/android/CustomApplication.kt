package fr.gstraymond.android

import android.app.Activity
import android.os.Bundle
import com.magic.card.search.commons.application.BaseApplication
import com.magic.card.search.commons.json.MapperUtil
import com.magic.card.search.commons.log.Log
import fr.gstraymond.biz.ElasticSearchClient
import fr.gstraymond.db.json.Decklist
import fr.gstraymond.db.json.JsonDeck
import fr.gstraymond.db.json.JsonHistoryDataSource
import fr.gstraymond.db.json.Wishlist
import fr.gstraymond.impex.DeckResolver
import fr.gstraymond.models.search.request.Request
import fr.gstraymond.network.ElasticSearchApi
import fr.gstraymond.network.ElasticSearchService
import fr.gstraymond.tools.VersionUtils
import fr.gstraymond.utils.getId
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CustomApplication : BaseApplication() {

    lateinit var elasticSearchClient: ElasticSearchClient
    lateinit var searchService: ElasticSearchService
    lateinit var jsonHistoryDataSource: JsonHistoryDataSource
    lateinit var wishlist: Wishlist
    lateinit var decklist: Decklist
    lateinit var jsonDeck: JsonDeck
    lateinit var deckResolver: DeckResolver
    lateinit var listsCardId: Map<String, List<String>>

    private val SEARCH_SERVER_HOST = "http://engine.mtg-search.com:8080"
    //private val SEARCH_SERVER_HOST = "192.168.1.15:9200"

    private val log = Log(this)

    override fun onCreate() {
        super.onCreate()
        val elasticSearchApi = buildRetrofit().create(ElasticSearchApi::class.java)
        searchService = ElasticSearchService(elasticSearchApi)

        jsonHistoryDataSource = JsonHistoryDataSource(this, objectMapper).apply { migrate() }

        elasticSearchClient = ElasticSearchClient(
                searchService,
                jsonHistoryDataSource,
                MapperUtil.fromType(objectMapper, Request::class.java))

        wishlist = Wishlist(this, objectMapper)
        decklist = Decklist(this, objectMapper)
        jsonDeck = JsonDeck(this, objectMapper)

        deckResolver = DeckResolver(searchService)

        refreshLists()

        registerActivityLifecycleCallbacks( object : ActivityLifecycleCallbacks{
            override fun onActivityPaused(activity: Activity) =
                    log.d("onActivityPaused: $activity")

            override fun onActivityStarted(activity: Activity) =
                    log.d("onActivityStarted: $activity")

            override fun onActivityDestroyed(activity: Activity) =
                    log.d("onActivityDestroyed: $activity")

            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) =
                    log.d("onActivitySaveInstanceState: $activity / $bundle")

            override fun onActivityStopped(activity: Activity) =
                    log.d("onActivityStopped: $activity")

            override fun onActivityCreated(activity: Activity, bundle: Bundle?) =
                    log.d("onActivityCreated: $activity / $bundle")

            override fun onActivityResumed(activity: Activity) =
                    log.d("onActivityResumed: $activity")
        })
    }

    private fun buildRetrofit(): Retrofit {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                    .header("User-Agent", "Android Java/" + VersionUtils.getOsVersion())
                    .header("Referer", VersionUtils.getAppName(this) + " - " + VersionUtils.getAppVersion())
                    .method(original.method(), original.body())
                    .build()

            chain.proceed(request)
        }
        httpClient.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS })

        return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(objectMapper))
                .client(httpClient.build())
                .baseUrl(SEARCH_SERVER_HOST).build()
    }

    fun refreshLists() {
        listsCardId =
                decklist.elems
                        .flatMap { deck ->
                            jsonDeck
                                    .load(deck.id.toString())
                                    .map { it.card.getId() to deck.id.toString() }
                        }
                        .plus(wishlist.elems.map { it.getId() to "wishlist" })
                        .groupBy { it.first }
                        .mapValues { it.value.map { it.second } }
    }
}
