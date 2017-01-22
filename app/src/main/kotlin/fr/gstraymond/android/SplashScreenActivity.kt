package fr.gstraymond.android

import android.app.SearchManager
import android.content.Intent
import android.content.Intent.ACTION_SEARCH
import android.os.Bundle
import com.magic.card.search.commons.json.MapperUtil
import fr.gstraymond.R
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SplashProcessor
import fr.gstraymond.models.search.response.SearchResult

class SplashScreenActivity : CustomActivity(R.layout.activity_splash) {

    private val GMS_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val options = when {
            listOf(ACTION_SEARCH, GMS_SEARCH).contains(intent.action) ->
                SearchOptions()
                        .updateQuery(intent.getStringExtra(SearchManager.QUERY))
                        .updateRandom(false)
                        .updateAddToHistory(true)
            else ->
                SearchOptions()
                        .updateRandom(true)
                        .updateAddToHistory(false)
        }

        SplashProcessor(this, options).execute()
    }

    fun startNextActivity(result: SearchResult) {
        startActivity {
            val resultAsString = MapperUtil.fromType(objectMapper, SearchResult::class.java).asJsonString(result)
            Intent(this, CardListActivity::class.java).apply {
                putExtra(CardListActivity.CARD_RESULT, resultAsString)
            }
        }
        finish()
    }
}
