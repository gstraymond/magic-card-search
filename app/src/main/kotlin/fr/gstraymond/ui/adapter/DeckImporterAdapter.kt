package fr.gstraymond.ui.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.view.ViewGroup
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.utils.inflate

class DeckImporterAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val lines = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            context.inflate(R.layout.array_adapter_deck_importer)
                    .run { object : RecyclerView.ViewHolder(this) {} }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as TextView).text = Html.fromHtml(lines[position])
    }

    override fun getItemCount() = lines.size

    fun addLine(line: String) {
        lines.add(line)
        notifyDataSetChanged()
    }
}