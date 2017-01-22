package fr.gstraymond.android

import android.app.Fragment
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.squareup.moshi.Moshi

abstract class CustomActivity(private val layoutId: Int) : AppCompatActivity() {

    val customApplication by lazy { application as CustomApplication }

    val objectMapper: Moshi by lazy { customApplication.objectMapper }

    val jsonHistoryDataSource by lazy { customApplication.jsonHistoryDataSource }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }

    open fun replaceFragment(fragment: Fragment, id: Int) {
        replaceFragment(fragment, id, null)
    }

    fun replaceFragment(fragment: Fragment, id: Int, bundle: Bundle?) {
        if (bundle != null) {
            fragment.arguments = bundle
        }
        fragmentManager.beginTransaction().replace(id, fragment).commitAllowingStateLoss()
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

    protected open fun buildContentViewEvent(): ContentViewEvent =
            ContentViewEvent()
                    .putContentName(javaClass.simpleName)
                    .putCustomAttribute("isTablet", isTablet.toString() + "")

    private val isTablet by lazy {
        resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Answers.getInstance().logContentView(buildContentViewEvent())
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

    protected fun startActivity(buildIntent: () -> Intent) {
        startActivity(buildIntent())
    }
}
