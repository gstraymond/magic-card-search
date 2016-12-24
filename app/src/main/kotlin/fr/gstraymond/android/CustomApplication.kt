package fr.gstraymond.android

import com.magic.card.search.commons.application.BaseApplication
import com.magic.card.search.commons.json.MapperUtil
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
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CustomApplication : BaseApplication() {

    lateinit var elasticSearchClient: ElasticSearchClient // FIXME remove me
    lateinit var searchService: ElasticSearchService
    lateinit var jsonHistoryDataSource: JsonHistoryDataSource
    lateinit var wishlist: Wishlist
    lateinit var decklist: Decklist
    lateinit var jsonDeck: JsonDeck
    lateinit var deckResolver: DeckResolver

    private val SEARCH_SERVER_HOST = "http://engine.mtg-search.com:8080"
    //private val SEARCH_SERVER_HOST = "192.168.1.15:9200"

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

        return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(objectMapper))
                .client(httpClient.build())
                .baseUrl(SEARCH_SERVER_HOST).build()
    }
}
