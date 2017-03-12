package fr.gstraymond.android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckDetailStatsAdapter
import fr.gstraymond.android.adapter.IntChart
import fr.gstraymond.android.adapter.StringChart
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.utils.app

class DeckDetailStatsFragment : Fragment() {

    private val deckDetailStatsAdapter by lazy { DeckDetailStatsAdapter(context) }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_deck_detail_stats, container, false).apply {
                (this as RecyclerView).apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = deckDetailStatsAdapter
                }
            }

    override fun onResume() {
        super.onResume()
        updateStats()
    }

    fun updateStats() {
        val deckId = activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA)
        val cardList = activity.app().cardListBuilder.build(deckId.toInt())
        val deckStats = DeckStats(cardList.all())
        deckDetailStatsAdapter.apply {
            elements = listOf(
                    "colors: ${deckStats.colors.joinToString()}",
                    "formats: ${deckStats.format}",
                    "cards: ${deckStats.deckSize}",
                    "sideboard: ${deckStats.sideboardSize}",
                    "price: ${deckStats.totalPrice}",
                    IntChart("Mana Curve", deckStats.manaCurve),
                    StringChart("Color Distribution", deckStats.colorDistribution),
                    StringChart("Type Distribution", deckStats.typeDistribution)
            )
            notifyDataSetChanged()
        }
    }
}