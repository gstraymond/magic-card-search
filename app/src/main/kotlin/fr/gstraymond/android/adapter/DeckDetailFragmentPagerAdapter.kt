package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
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
        FragmentStatePagerAdapter(fragmentManager) {

    var deckCardCallback: DeckCardCallback? = null

    val formatCallback = object : DeckDetailActivity.FormatCallback {
        override fun formatChanged() {
            deckDetailCardsFragment.formatChanged()
        }

    }

    private val callbacks = object : DeckCardCallback {
        override fun multChanged(deckCard: DeckCard, from: DeckCardCallback.FROM, deck: Int, sideboard: Int) =
                onMultChanged(deckCard, from, deck, sideboard)

        override fun cardClick(deckCard: DeckCard) = Unit
    }

    private fun onMultChanged(deckCard: DeckCard, from: DeckCardCallback.FROM, deck: Int, sideboard: Int) {
        deckDetailStatsFragment.updateStats()
        when (from) {
            DECK -> deckDetailSideboardFragment.multChanged(deckCard, from, deck, sideboard)
            SB -> deckDetailCardsFragment.multChanged(deckCard, from, deck, sideboard)
        }
        deckCardCallback?.multChanged(deckCard, from, deck, sideboard)
    }

    private val pageTitles = listOf("", "", context.getString(R.string.deck_tab_stats), context.getString(R.string.deck_tab_hand))
    private val deckDetailCardsFragment = DeckDetailCardsFragment().apply { deckCardCallback = callbacks }
    private val deckDetailSideboardFragment = DeckDetailCardsFragment().apply {
        deckCardCallback = callbacks
        sideboard = true
    }
    private val deckDetailStatsFragment = DeckDetailStatsFragment()

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