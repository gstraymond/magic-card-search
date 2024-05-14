package fr.gstraymond.network

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.squareup.moshi.Moshi
import fr.gstraymond.BuildConfig
import fr.gstraymond.tools.VersionUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException


object RetrofitBuilder {

    private const val SEARCH_SERVER_HOST = "https://search.mtg-search.com"
    //private const val SEARCH_SERVER_HOST = "http://192.168.1.14:9200" // and update android manifest

    fun buildRetrofit(moshi: Moshi, context: Context): Retrofit {

        @SuppressLint("CustomX509TrustManager")
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, SecureRandom())
        }

        val httpClient = OkHttpClient.Builder()

        // old devices doesn't recognize let's encrypt certificate
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            httpClient.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            httpClient.hostnameVerifier { _, _ -> true }
        }

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