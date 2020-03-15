package fr.gstraymond.android.adapter

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.ViewGroup
import fr.gstraymond.R
import fr.gstraymond.android.DeckDetailCardsFragment
import fr.gstraymond.android.DeckDetailHandFragment
import fr.gstraymond.android.DeckDetailStatsFragment
import fr.gstraymond.models.Board
import fr.gstraymond.models.Board.*

class DeckDetailFragmentPagerAdapter(fragmentManager: FragmentManager,
                                     context: Context) :
        FragmentPagerAdapter(fragmentManager) {

    private var cards: DeckDetailCardsFragment? = null
    private var sb: DeckDetailCardsFragment? = null
    private var maybe: DeckDetailCardsFragment? = null
    private var stats: DeckDetailStatsFragment? = null

    fun onMultChanged(from: Board, position: Int) {
        stats?.updateStats()
        when (from) {
            DECK -> {
                sb?.multChanged(from, position)
                maybe?.multChanged(from, position)
            }
            SB -> {
                cards?.multChanged(from, position)
                maybe?.multChanged(from, position)
            }
            MAYBE -> {
                cards?.multChanged(from, position)
                sb?.multChanged(from, position)
            }
        }
    }

    fun formatChanged() {
        cards?.formatChanged()
        sb?.formatChanged()
        maybe?.formatChanged()
    }

    private val pageTitles = listOf("", "", "", context.getString(R.string.deck_tab_stats), context.getString(R.string.deck_tab_hand))

    override fun getCount() = pageTitles.size

    override fun getItem(position: Int) = when (position) {
        0 -> DeckDetailCardsFragment()
        1 -> DeckDetailCardsFragment().apply { board = SB }
        2 -> DeckDetailCardsFragment().apply { board = MAYBE }
        3 -> DeckDetailStatsFragment()
        else -> DeckDetailHandFragment()
    }

    override fun instantiateItem(container: ViewGroup, position: Int) =
            super.instantiateItem(container, position).apply {
                when (position) {
                    0 -> cards = this as DeckDetailCardsFragment
                    1 -> sb = this as DeckDetailCardsFragment
                    2 -> maybe = this as DeckDetailCardsFragment
                    3 -> stats = this as DeckDetailStatsFragment
                }
            }

    override fun getPageTitle(position: Int) = pageTitles[position]
}