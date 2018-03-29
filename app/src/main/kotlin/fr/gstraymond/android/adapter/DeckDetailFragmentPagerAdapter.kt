package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import fr.gstraymond.R
import fr.gstraymond.android.DeckDetailCardsFragment
import fr.gstraymond.android.DeckDetailHandFragment
import fr.gstraymond.android.DeckDetailStatsFragment
import fr.gstraymond.android.adapter.DeckCardCallback.FROM.DECK
import fr.gstraymond.android.adapter.DeckCardCallback.FROM.SB

class DeckDetailFragmentPagerAdapter(fragmentManager: FragmentManager,
                                     context: Context) :
        FragmentPagerAdapter(fragmentManager) {

    private var cards: DeckDetailCardsFragment? = null
    private var sb: DeckDetailCardsFragment? = null
    private var stats: DeckDetailStatsFragment? = null

    fun onMultChanged(from: DeckCardCallback.FROM, position: Int) {
        stats?.updateStats()
        when (from) {
            DECK -> sb?.multChanged(from, position)
            SB -> cards?.multChanged(from, position)
        }
    }

    fun formatChanged() {
        cards?.formatChanged()
        sb?.formatChanged()
    }

    private val pageTitles = listOf("", "", context.getString(R.string.deck_tab_stats), context.getString(R.string.deck_tab_hand))

    override fun getCount() = pageTitles.size

    override fun getItem(position: Int) = when (position) {
        0 -> DeckDetailCardsFragment()
        1 -> DeckDetailCardsFragment().apply { sideboard = true }
        2 -> DeckDetailStatsFragment()
        else -> DeckDetailHandFragment()
    }

    override fun instantiateItem(container: ViewGroup, position: Int) =
            super.instantiateItem(container, position).apply {
                when (position) {
                    0 -> cards = this as DeckDetailCardsFragment
                    1 -> sb = this as DeckDetailCardsFragment
                    2 -> stats = this as DeckDetailStatsFragment
                }
            }

    override fun getPageTitle(position: Int) = pageTitles[position]
}