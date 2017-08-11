package fr.gstraymond.ui.adapter.card.detail

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.CardListActivity
import fr.gstraymond.biz.CastingCostImageGetter
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.constants.FacetConst
import fr.gstraymond.constants.FacetConst.*
import fr.gstraymond.glide.CardLoader
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.tools.*
import fr.gstraymond.utils.find
import fr.gstraymond.utils.startActivity

class CardView(val context: Context,
               val callbacks: CardDetailAdapter.Callbacks) : View<Card>(context, R.layout.card_detail) {

    private val castingCostFormatter = CastingCostFormatter()
    private val descFormatter = DescriptionFormatter()
    private val formatFormatter = FormatFormatter(context)
    private val ptFormatter = PowerToughnessFormatter()
    private val typeFormatter = TypeFormatter()
    private val imageGetter = CastingCostImageGetter.large(context)

    override fun getView(item: Card, view: android.view.View): android.view.View {
        val ccptView = view.find<TextView>(R.id.card_textview_ccpt)
        val typeView = view.find<TextView>(R.id.card_textview_type)
        val pictureView = view.find<ImageView>(R.id.card_picture)
        val descView = view.find<TextView>(R.id.card_textview_description)
        val formatsView = view.find<TextView>(R.id.card_textview_formats)
        val altView = view.find<Button>(R.id.card_alt)

        val ccpt = formatCCPT(item)
        if (ccpt.toString().isEmpty()) ccptView.visibility = android.view.View.GONE
        else ccptView.text = ccpt

        val type = typeFormatter.format(item)
        if (type.isEmpty()) typeView.visibility = android.view.View.GONE
        else typeView.text = type

        val images = item.publications.map { it.image }
        val url = images.firstOrNull { it != null }
        val urlPosition = images.indexOfFirst { it != null }

        if (url != null) CardLoader(url, item, pictureView).load(context)
        else pictureView.visibility = android.view.View.GONE

        val finalPosition = urlPosition
        pictureView.setOnClickListener { callbacks.onImageClick(finalPosition) }

        formatsView.text = formatFormatter.format(item)

        val desc = descFormatter.format(item, true)
        if (desc.isEmpty()) descView.visibility = android.view.View.GONE
        else descView.text = Html.fromHtml(desc, imageGetter, null)

        if (item.altTitles.isEmpty()) {
            altView.visibility = android.view.View.GONE
        } else {
            altView.text = item.altTitles.joinToString("\n")
            altView.setOnClickListener {
                context.startActivity {
                    val searchOptions = SearchOptions(
                            query = item.title,
                            facets = mapOf(LAYOUT to listOf(item.layout)))
                    CardListActivity.getIntent(context, searchOptions)
                }
            }
        }

        return view
    }

    private fun formatCCPT(card: Card): Spanned {
        val cc = formatCC(card)
        val pt = ptFormatter.format(card).run {
            if (isEmpty() && card.loyalty != null) card.loyalty
            else this
        }
        return Html.fromHtml(formatCC_PT(cc, pt), imageGetter, null)
    }

    private fun formatCC_PT(cc: String, pt: String) = when {
        cc.isEmpty() -> pt
        pt.isEmpty() -> cc
        else -> "$cc — $pt"
    }

    private fun formatCC(card: Card) = when (card.castingCost) {
        null -> ""
        else -> castingCostFormatter.format(card.castingCost)
    }
}