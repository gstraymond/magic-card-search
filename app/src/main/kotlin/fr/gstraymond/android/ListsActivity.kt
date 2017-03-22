package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import fr.gstraymond.R
import fr.gstraymond.android.adapter.ListsFragmentPagerAdapter
import fr.gstraymond.utils.find

class ListsActivity : CustomActivity(R.layout.activity_lists) {

    companion object {
        fun getIntent(context: Context) =
                Intent(context, ListsActivity::class.java)
    }

    private val titles = listOf(
            R.string.wishlist_title,
            R.string.decks_title)

    private val icons = listOf(
            R.drawable.ic_star_white_18dp,
            R.drawable.ic_bookmark_white_18dp)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(titles[0])

        val viewPager = find<ViewPager>(R.id.viewpager)
        viewPager.adapter = ListsFragmentPagerAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                setTitle(titles[position])
            }
        })

        find<TabLayout>(R.id.sliding_tabs).apply {
            setupWithViewPager(viewPager)
            (0..tabCount - 1).forEach {
                getTabAt(it)?.setIcon(icons[it])
            }
        }
    }

    open class SimpleOnPageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {}

    }
}
