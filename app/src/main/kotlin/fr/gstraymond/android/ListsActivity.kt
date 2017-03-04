package fr.gstraymond.android

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import fr.gstraymond.R
import fr.gstraymond.android.adapter.ListsFragmentPagerAdapter
import fr.gstraymond.utils.find


class ListsActivity : CustomActivity(R.layout.activity_lists) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewPager = find<ViewPager>(R.id.viewpager)
        viewPager.adapter = ListsFragmentPagerAdapter(
                supportFragmentManager,
                this,
                app.wishList,
                app.deckList,
                app.cardListBuilder)

        find<TabLayout>(R.id.sliding_tabs).setupWithViewPager(viewPager)
    }
}
