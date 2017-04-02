package fr.gstraymond.android

import android.app.SearchManager
import android.content.Intent.ACTION_SEARCH
import android.os.Bundle
import com.magic.card.search.commons.json.MapperUtil
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SplashProcessor
import fr.gstraymond.models.search.response.SearchResult
import fr.gstraymond.utils.startActivity
import java.util.*

class SplashScreenActivity : CustomActivity(R.layout.activity_splash) {

    private val GMS_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION"
    private val started = Date()
    private val log = Log(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val options = when {
            listOf(ACTION_SEARCH, GMS_SEARCH).contains(intent.action) ->
                SearchOptions(
                        query = intent.getStringExtra(SearchManager.QUERY),
                        random = false,
                        addToHistory = true)
            else ->
                SearchOptions(size = 0,
                        addToHistory = false)
        }

        SplashProcessor(this, options).execute()
    }

    fun startNextActivity(result: SearchResult?) {
        val remainingTime = 1000 - (Date().time - started.time)
        log.d("remaining time: $remainingTime")
        if (remainingTime > 0) Thread.sleep(remainingTime)

        startActivity {
            val resultAsString = MapperUtil.fromType(objectMapper, SearchResult::class.java).asJsonString(result)
            CardListActivity.getIntent(this, resultAsString)
        }
        finish()
    }
}
