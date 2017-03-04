package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import fr.gstraymond.R
import fr.gstraymond.android.DeckListFragment
import fr.gstraymond.android.WishListFragment
import fr.gstraymond.db.json.CardListBuilder
import fr.gstraymond.db.json.DeckList
import fr.gstraymond.db.json.WishList


class ListsFragmentPagerAdapter(fragmentManager: FragmentManager,
                                context: Context,
                                wishList: WishList,
                                deckList: DeckList,
                                cardListBuilder: CardListBuilder) : FragmentStatePagerAdapter(fragmentManager) {

    // FIXME translate
    private val tabTitles = arrayOf(context.getString(R.string.wishlist_title), "Decks")
    private val wishListFragment = WishListFragment(wishList)
    private val deckListFragment = DeckListFragment(deckList, cardListBuilder)

    override fun getCount() = tabTitles.size

    override fun getItem(position: Int) = when (position) {
        0 -> wishListFragment
        else -> deckListFragment
    }

    override fun getPageTitle(position: Int) = tabTitles[position]

}