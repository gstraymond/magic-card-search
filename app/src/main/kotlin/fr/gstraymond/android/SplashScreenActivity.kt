package fr.gstraymond.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.utils.startActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity {
            CardListActivity.getIntent(this, SearchOptions(size = 0))
        }
        finish()
    }
}
