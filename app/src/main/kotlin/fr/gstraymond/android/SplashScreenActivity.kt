package fr.gstraymond.android

import android.os.Bundle
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.utils.startActivity
import java.util.*

class SplashScreenActivity : CustomActivity(R.layout.activity_splash) {

    private val started = Date()
    private val log = Log(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startNextActivity()
    }

    // FIXME remove me
    fun startNextActivity() {
        val remainingTime = 1000 - (Date().time - started.time)
        log.d("remaining time: $remainingTime")
        if (remainingTime > 0) Thread.sleep(remainingTime)

        startActivity {
            CardListActivity.getIntent(this, SearchOptions(size = 0))
        }
        finish()
    }
}
