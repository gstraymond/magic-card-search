package fr.gstraymond.android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import fr.gstraymond.R
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.utils.app
import fr.gstraymond.utils.color
import fr.gstraymond.utils.find

class DeckDetailStatsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_deck_detail_stats, container, false)

    override fun onResume() {
        super.onResume()
        updateStats()
    }

    fun updateStats() {
        val deckId = activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA)
        val cardList = activity.app().cardListBuilder.build(deckId.toInt())
        val deckStats = DeckStats(cardList.all())
        activity.find<TextView>(R.id.deck_colors).text = "colors: ${deckStats.colors.joinToString()}"
        activity.find<TextView>(R.id.deck_formats).text = "formats: ${deckStats.format}"
        activity.find<TextView>(R.id.deck_cards).text = "cards: ${deckStats.deckSize}"
        activity.find<TextView>(R.id.deck_sideboard).text = "sidebard: ${deckStats.sideboardSize}"
        activity.find<TextView>(R.id.deck_price).text = "price: ${deckStats.totalPrice}"
        val barChart = activity.find<BarChart>(R.id.mana_curve)
        val entries = deckStats.manaCurve.map { (k, v) -> BarEntry(k.toFloat(), v.toFloat()) }
        barChart.description = null
        barChart.legend.textColor = resources.color(android.R.color.white)
        barChart.legend.textSize = resources.getDimension(R.dimen.chartLabelSize)
        barChart.data = BarData(BarDataSet(entries, "Mana Curve").apply {
            color = resources.color(R.color.gold)
            valueTextColor = resources.color(android.R.color.white)
            setValueFormatter { fl, _, _, _ -> "${fl.toInt()}" }
            valueTextSize = resources.getDimension(R.dimen.chartLabelSize)
        })
        barChart.xAxis.apply {
            setDrawGridLines(false)
            textColor = resources.color(android.R.color.white)
            textSize = resources.getDimension(R.dimen.chartLabelSize)
            position = XAxis.XAxisPosition.BOTTOM
        }
        barChart.axisLeft.apply {
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }
        barChart.axisRight.apply {
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }
        barChart.invalidate()
    }
}