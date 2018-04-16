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
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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
                when (element) {
                    is StringChart -> {
                        val data = element.data.toList().sortedBy { -it.second }.take(5)
                        val keys = data.map { it.first }
                        val entries = data.map { (k, v) -> BarEntry(keys.indexOf(k).toFloat(), v.toFloat()) }

                        val colors =
                                if (keys.all { colorMap.containsKey(it) }) keys.map { colorMap[it]!! }
                                else listOf()
                        barChart.data = BarData(BarDataSet(entries, element.name).apply {
                            styleDataSet(this, position, colors)
                            setValueFormatter { fl, _, _, _ -> "${fl.toInt()}" }
                        })
                        styleChart(barChart, colors) { fl, _ -> keys.elementAtOrNull(fl.toInt()) ?: "" }
                    }
                    is IntChart -> {
                        val entries = element.data.map { (k, v) -> BarEntry(k.toFloat(), v.toFloat()) }
                        barChart.data = BarData(BarDataSet(entries, element.name).apply {
                            styleDataSet(this, position)
                            setValueFormatter { fl, _, _, _ -> "${fl.toInt()}" }
                        })
                        styleChart(barChart) { fl, _ ->
                            fl.toInt().run {
                                if (this < 7) "$this"
                                else "$this+"
                            }
                        }
                    }
                }
                barChart.invalidate()
            }
        }
    }

    private val colorMap = mapOf(
            "Black" to R.color.black,
            "Blue" to R.color.blue,
            "Green" to R.color.green,
            "Red" to R.color.red,
            "White" to R.color.white,
            "Noir" to R.color.black,
            "Bleu" to R.color.blue,
            "Vert" to R.color.green,
            "Rouge" to R.color.red,
            "Blanc" to R.color.white
    )

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

    private val CHART_TEXT_SIZE = 16f

    private fun styleChart(chart: BarChart,
                           colors: List<Int> = listOf(),
                           f: (Float, AxisBase) -> String) {
        chart.description = null
        chart.isDoubleTapToZoomEnabled = false
        chart.setPinchZoom(false)
        chart.setScaleEnabled(false)
        chart.legend.textColor = resources.color(android.R.color.white)
        chart.legend.textSize = CHART_TEXT_SIZE
        chart.xAxis.apply {
            setDrawGridLines(false)
            granularity = 1.0f
            isGranularityEnabled = true
            isEnabled = colors.isEmpty()
            textColor = resources.color(android.R.color.white)
            textSize = CHART_TEXT_SIZE
            position = XAxis.XAxisPosition.BOTTOM
            setValueFormatter(f)
        }
        chart.axisLeft.apply {
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
            axisMinimum = 0f
        }
        chart.axisRight.apply {
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
            axisMinimum = 0f
        }
    }

    private fun styleDataSet(dataSet: BarDataSet, position: Int, colors: List<Int> = listOf()) {
        if (colors.isNotEmpty()) {
            val coloIds = colors.map { resources.color(it) }
            dataSet.colors = coloIds
            dataSet.setValueTextColors(coloIds)
        } else {
            dataSet.color = resources.color(if (position % 2 == 0) R.color.gold else R.color.colorPrimary)
            dataSet.valueTextColor = resources.color(android.R.color.white)
        }
        dataSet.valueTextSize = CHART_TEXT_SIZE
    }
}

class TextViewHolder(view: View) : RecyclerView.ViewHolder(view)
class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view)

data class StringChart(val name: String, val data: Map<String, Int>)
data class IntChart(val name: String, val data: Map<Int, Int>)