package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckDetailStatsAdapter.ItemTypes.CHART
import fr.gstraymond.android.adapter.DeckDetailStatsAdapter.ItemTypes.TEXT
import fr.gstraymond.utils.color
import fr.gstraymond.utils.find

class DeckDetailStatsAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val resources = context.resources

    lateinit var elements: List<Any>

    enum class ItemTypes { TEXT, CHART }

    override fun getItemCount() = elements.size

    override fun getItemViewType(position: Int) = when (elements[position]) {
        is StringChart, is IntChart -> CHART
        is String, is Spanned -> TEXT
        else -> throw RuntimeException("getItemViewType: ${elements[position]}")
    }.ordinal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val element = elements[position]
        when (holder) {
            is TextViewHolder -> holder.itemView
                    .find<TextView>(R.id.array_adapter_deck_stat_text)
                    .apply {
                        when (element) {
                            is String -> text = element
                            is Spanned -> text = element
                        }
                    }
            is ChartViewHolder -> {
                val barChart = holder.itemView as BarChart
                val chart = element
                when (chart) {
                    is StringChart -> {
                        val keys = chart.data.keys.toList()
                        val entries = chart.data.map { (k, v) -> BarEntry(keys.indexOf(k).toFloat(), v.toFloat()) }
                        barChart.data = BarData(BarDataSet(entries, chart.name).apply {
                            styleDataSet(this)
                            setValueFormatter { fl, _, _, _ -> "${fl.toInt()}" }
                        })
                        styleChart(barChart) { fl, _ -> keys[fl.toInt()] }
                    }
                    is IntChart -> {
                        val entries = chart.data.map { (k, v) -> BarEntry(k.toFloat(), v.toFloat()) }
                        barChart.data = BarData(BarDataSet(entries, chart.name).apply {
                            styleDataSet(this)
                            setValueFormatter { fl, _, _, _ -> "${fl.toInt()}" }
                        })
                        styleChart(barChart) { fl, _ -> "${fl.toInt()}" }
                    }
                }
                barChart.invalidate()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TEXT.ordinal ->
                LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.array_adapter_deck_stat_text, parent, false)
                        .run { TextViewHolder(this) }
            CHART.ordinal ->
                LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.array_adapter_deck_stat_chart, parent, false)
                        .run { ChartViewHolder(this) }
            else -> throw RuntimeException("onCreateViewHolder: $viewType")
        }
    }

    private fun styleChart(chart: BarChart, f: (Float, AxisBase) -> String) {
        chart.description = null
        chart.legend.textColor = resources.color(android.R.color.white)
        chart.legend.textSize = resources.getDimension(R.dimen.chartLabelSize)
        chart.xAxis.apply {
            setDrawGridLines(false)
            granularity = 1.0f
            isGranularityEnabled = true
            textColor = resources.color(android.R.color.white)
            textSize = resources.getDimension(R.dimen.chartLabelSize)
            position = XAxis.XAxisPosition.BOTTOM
            setValueFormatter(f)
        }
        chart.axisLeft.apply {
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }
        chart.axisRight.apply {
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }
    }

    private fun styleDataSet(dataSet: BarDataSet) {
        dataSet.color = resources.color(R.color.gold)
        dataSet.valueTextColor = resources.color(android.R.color.white)
        dataSet.valueTextSize = resources.getDimension(R.dimen.chartLabelSize)
    }
}

class TextViewHolder(view: View) : RecyclerView.ViewHolder(view)
class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view)

data class StringChart(val name: String, val data: Map<String, Int>)
data class IntChart(val name: String, val data: Map<Int, Int>)