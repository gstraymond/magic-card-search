package fr.gstraymond.android

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.squareup.moshi.Moshi
import fr.gstraymond.utils.app

abstract class CustomActivity(private val layoutId: Int) : AppCompatActivity() {

    val jsonHistoryDataSource by lazy { app().historyList }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }

    open fun replaceFragment(fragment: Fragment, id: Int) {
        replaceFragment(fragment, id, null)
    }

    fun replaceFragment(fragment: Fragment, id: Int, bundle: Bundle?) {
        bundle?.apply { fragment.arguments = this }
        supportFragmentManager.beginTransaction().replace(id, fragment).commitAllowingStateLoss()
    }

    protected fun actionBarSetDisplayHomeAsUpEnabled(bool: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(bool)
    }

    protected fun actionBarSetHomeButtonEnabled(bool: Boolean) {
        supportActionBar?.setHomeButtonEnabled(bool)
    }

    protected fun actionBarSetTitle(titleId: Int) {
        supportActionBar?.setTitle(titleId)
    }

    private val isTablet by lazy {
        resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
