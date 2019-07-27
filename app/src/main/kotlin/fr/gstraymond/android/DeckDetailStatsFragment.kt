package fr.gstraymond.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.DeckDetailActivity.Companion.DECK_EXTRA
import fr.gstraymond.android.adapter.DeckDetailStatsAdapter
import fr.gstraymond.android.adapter.IntChart
import fr.gstraymond.android.adapter.StringChart
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.gone
import fr.gstraymond.utils.visible

class DeckDetailStatsFragment : Fragment() {

    private val deckDetailStatsAdapter by lazy { DeckDetailStatsAdapter(context!!) }

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_deck_detail_stats, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.find<RecyclerView>(R.id.deck_detail_stats_recyclerview).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = deckDetailStatsAdapter
        }
        emptyText = view.find(R.id.deck_detail_stats_empty)
    }

    override fun onResume() {
        super.onResume()
        updateStats()
    }

    fun updateStats() {
        if (activity == null) return

        val deckId = activity!!.intent.getStringExtra(DECK_EXTRA)
        val deck = activity!!.app().deckList.getByUid(deckId)!!
        val cardList = activity!!.app().cardListBuilder.build(deckId.toInt())
        if (cardList.isEmpty()) {
            recyclerView.gone()
            emptyText.visible()
        } else {
            recyclerView.visible()
            emptyText.gone()
            val deckStats = DeckStats(cardList.all(), deck.isCommander())
            deckDetailStatsAdapter.apply {
                val abilitiesCharts = deckStats.abilitiesCount.run {
                    if (isEmpty()) listOf()
                    else listOf(StringChart(getText(R.string.abilities).toString(), this))
                }

                val colorDistribution =
                        if (deck.colors.size < 2) listOf()
                        else listOf(StringChart(resources.getString(R.string.stats_color_distribution), deckStats.colorDistribution(context!!)))

                val typeDistribution = listOf(StringChart(resources.getString(R.string.stats_type_distribution), deckStats.typeDistribution(context!!)))

                elements = listOf(
                        getText(R.string.stats_total_price, "${deckStats.totalPrice}"),
                        IntChart(resources.getString(R.string.stats_mana_curve), deckStats.manaCurve)
                ) + colorDistribution + typeDistribution + abilitiesCharts

                notifyDataSetChanged()
            }
        }
    }

    private fun getText(textId: Int, vararg args: String) =
            String.format(resources.getString(textId), *args)
}