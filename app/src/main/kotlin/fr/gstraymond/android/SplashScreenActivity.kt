package fr.gstraymond.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.utils.startActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity {
            CardListActivity.getIntent(this, SearchOptions.START_SEARCH_OPTIONS())
        }
        finish()
    }
}
