package fr.gstraymond.android.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import fr.gstraymond.android.DeckDetailCardsFragment
import fr.gstraymond.android.DeckDetailStatsFragment
import fr.gstraymond.models.DeckLine

class DeckDetailFragmentPagerAdapter(fragmentManager: FragmentManager) :
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

    // FIXME translate
    private val pageTitles = listOf("Cards", "Stats")
    private val deckDetailCardsFragment = DeckDetailCardsFragment().apply { deckLineCallback = callbacks }
    private val deckDetailStatsFragment = DeckDetailStatsFragment()
    private val fragments = listOf(deckDetailCardsFragment, deckDetailStatsFragment)

    override fun getCount() = fragments.size

    override fun getItem(position: Int) = fragments[position]

    override fun getPageTitle(position: Int) = pageTitles[position]
}