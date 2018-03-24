package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import fr.gstraymond.R
import fr.gstraymond.android.DeckDetailActivity
import fr.gstraymond.android.DeckDetailCardsFragment
import fr.gstraymond.android.DeckDetailHandFragment
import fr.gstraymond.android.DeckDetailStatsFragment
import fr.gstraymond.android.adapter.DeckCardCallback.FROM.DECK
import fr.gstraymond.android.adapter.DeckCardCallback.FROM.SB
import fr.gstraymond.models.DeckCard

class DeckDetailFragmentPagerAdapter(fragmentManager: FragmentManager,
                                     context: Context) :
        FragmentPagerAdapter(fragmentManager) {

    var deckCardCallback: DeckCardCallback? = null

    val formatCallback = object : DeckDetailActivity.FormatCallback {
        override fun formatChanged() = deckDetailCardsFragment.formatChanged() // FIXME handle SB
    }

    private val callbacks = object : DeckCardCallback {
        override fun multChanged(from: DeckCardCallback.FROM, position: Int) =
                onMultChanged(from, position)

        override fun cardClick(deckCard: DeckCard) {}
    }

    private fun onMultChanged(from: DeckCardCallback.FROM, position: Int) {
        deckDetailStatsFragment.updateStats()
        when (from) {
            DECK -> deckDetailSideboardFragment.multChanged(from, position)
            SB -> deckDetailCardsFragment.multChanged(from, position)
        }
        deckCardCallback?.multChanged(from, position)
    }

    private val pageTitles = listOf("", "", context.getString(R.string.deck_tab_stats), context.getString(R.string.deck_tab_hand))
    private val deckDetailStatsFragment = DeckDetailStatsFragment()
    private val deckDetailCardsFragment = DeckDetailCardsFragment().apply { deckCardCallback = callbacks }
    private val deckDetailSideboardFragment = DeckDetailCardsFragment().apply {
        deckCardCallback = callbacks
        sideboard = true
    }

    private val fragments = listOf(
            deckDetailCardsFragment,
            deckDetailSideboardFragment,
            deckDetailStatsFragment,
            DeckDetailHandFragment()
    )

    override fun getCount() = fragments.size

    override fun getItem(position: Int) = fragments[position]

    override fun getPageTitle(position: Int) = pageTitles[position]
}