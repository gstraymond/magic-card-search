package fr.gstraymond.network

import android.content.Context
import com.squareup.moshi.Moshi
import fr.gstraymond.BuildConfig
import fr.gstraymond.tools.VersionUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitBuilder {

    private val SEARCH_SERVER_HOST = "http://engine.mtg-search.com"
    //private val SEARCH_SERVER_HOST = "http://192.168.1.15:9200"

    fun buildRetrofit(moshi: Moshi, context: Context): Retrofit {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                    .header("User-Agent", "Android Java/" + VersionUtils.getOsVersion())
                    .header("Referer", VersionUtils.getAppName(context) + " - " + VersionUtils.getAppVersion())
                    .method(original.method(), original.body())
                    .build()

            chain.proceed(request)
        }

        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS })
        }

        return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(httpClient.build())
                .baseUrl(SEARCH_SERVER_HOST).build()
    }
}