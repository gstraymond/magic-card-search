package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import fr.gstraymond.R
import fr.gstraymond.android.DeckDetailCardsFragment
import fr.gstraymond.android.DeckDetailStatsFragment
import fr.gstraymond.models.DeckLine

class DeckDetailFragmentPagerAdapter(fragmentManager: FragmentManager, context: Context) :
        FragmentStatePagerAdapter(fragmentManager) {

    private val callbacks = object : DeckLineCallback {
        override fun multChanged(deckLine: DeckLine, mult: Int) {
            deckDetailStatsFragment.updateStats()
        }

        override fun sideboardChanged(deckLine: DeckLine, sideboard: Boolean) {
            deckDetailStatsFragment.updateStats()
        }

        override fun cardClick(deckLine: DeckLine) {
        }
    }

    private val pageTitles = listOf(context.getString(R.string.deck_tab_cards), context.getString(R.string.deck_tab_stats))
    private val deckDetailCardsFragment = DeckDetailCardsFragment().apply { deckLineCallback = callbacks }
    private val deckDetailStatsFragment = DeckDetailStatsFragment()

    // FIXME todo data structure for fragment and title
    private val fragments = listOf(deckDetailCardsFragment, deckDetailStatsFragment)

    override fun getCount() = fragments.size

    override fun getItem(position: Int) = fragments[position]

    override fun getPageTitle(position: Int) = pageTitles[position]
}