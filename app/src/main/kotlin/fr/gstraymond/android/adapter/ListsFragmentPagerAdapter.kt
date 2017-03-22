package fr.gstraymond.android.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import fr.gstraymond.android.DeckListFragment
import fr.gstraymond.android.WishListFragment


class ListsFragmentPagerAdapter(fragmentManager: FragmentManager) :
        FragmentStatePagerAdapter(fragmentManager) {

    private val fragments = listOf(WishListFragment(), DeckListFragment())

    override fun getCount() = fragments.size

    override fun getItem(position: Int) = fragments[position]
}