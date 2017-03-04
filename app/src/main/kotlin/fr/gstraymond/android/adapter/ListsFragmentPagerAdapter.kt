package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import fr.gstraymond.R
import fr.gstraymond.android.DeckListFragment
import fr.gstraymond.android.WishListFragment


class ListsFragmentPagerAdapter(fragmentManager: FragmentManager,
                                context: Context) : FragmentStatePagerAdapter(fragmentManager) {

    // FIXME translate
    private val tabTitles = arrayOf(context.getString(R.string.wishlist_title), "Decks")

    override fun getCount() = tabTitles.size

    override fun getItem(position: Int) = when (position) {
        0 -> WishListFragment()
        else -> DeckListFragment()
    }

    override fun getPageTitle(position: Int) = tabTitles[position]

}