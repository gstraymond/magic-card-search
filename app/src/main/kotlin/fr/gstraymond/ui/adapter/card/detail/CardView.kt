package fr.gstraymond.ui.adapter.card.detail

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.CardListActivity
import fr.gstraymond.android.prefs
import fr.gstraymond.biz.CastingCostImageGetter
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.constants.FacetConst.LAYOUT
import fr.gstraymond.glide.CardLoader
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.Ruling
import fr.gstraymond.tools.*
import fr.gstraymond.utils.find
import fr.gstraymond.utils.gone
import fr.gstraymond.utils.startActivity
import fr.gstraymond.utils.visible

class CardView(val context: Context,
               val callbacks: CardDetailAdapter.InnerCallbacks) : View<Card>(context, R.layout.card_detail) {

    private val castingCostFormatter = CastingCostFormatter()
    private val descFormatter = DescriptionFormatter()
    private val formatFormatter = FormatFormatter(context)
    private val ptFormatter = PowerToughnessFormatter()
    private val typeFormatter = TypeFormatter()
    private val imageGetter = CastingCostImageGetter.large(context)

    private var showPaper = prefs.paperPrice

    override fun getView(card: Card, view: android.view.View): android.view.View {
        val ccptView = view.find<TextView>(R.id.card_textview_ccpt)
        val typeView = view.find<TextView>(R.id.card_textview_type)
        val pictureView = view.find<ImageView>(R.id.card_picture)
        val descView = view.find<TextView>(R.id.card_textview_description)
        val firstRulingView = view.find<TextView>(R.id.card_textview_first_ruling)
        val rulingView = view.find<TextView>(R.id.card_textview_ruling)
        val formatsView = view.find<TextView>(R.id.card_textview_formats)
        val altView = view.find<Button>(R.id.card_alt)
        val foilHintView = view.find<TextView>(R.id.card_textview_foil_hint)
        val showPaperView = view.find<TextView>(R.id.card_textview_show_paper)

        val ccpt = formatCCPT(card)
        if (ccpt.toString().isEmpty()) ccptView.visibility = android.view.View.GONE
        else ccptView.text = ccpt

        val type = typeFormatter.format(card)
        if (type.isEmpty()) typeView.visibility = android.view.View.GONE
        else typeView.text = type

        val images = card.publications.map { it.image }
        val url = images.firstOrNull { it != null }
        val urlPosition = images.indexOfFirst { it != null }

        if (url != null) CardLoader(url, card, pictureView).load(context)
        else pictureView.visibility = android.view.View.GONE

        pictureView.setOnClickListener { callbacks.onImageClick(urlPosition) }

        formatsView.text = formatFormatter.format(card)

        val desc = descFormatter.format(card, true)
        if (desc.isEmpty()) descView.gone()
        else descView.text = Html.fromHtml(desc, imageGetter, null)

        when {
            card.ruling.isEmpty() -> {
                firstRulingView.gone()
                rulingView.gone()
            }

            card.ruling.size == 1 -> {
                firstRulingView.gone()
                rulingView.visible()
                val ruling = card.ruling.first()
                rulingView.text = Html.fromHtml(formatRuling(ruling))
            }

            else -> {
                firstRulingView.visible()
                val ruling = card.ruling.first()
                val rulingText = String.format(context.resources.getString(R.string.deck_detail_ruling), formatRuling(ruling), card.ruling.size - 1)
                firstRulingView.text = Html.fromHtml(rulingText)
                firstRulingView.setOnFocusChangeListener { _, _ ->
                    rulingView.visible()
                    firstRulingView.gone()
                }
                rulingView.gone()
                rulingView.text = Html.fromHtml(
                        card.ruling.zip(listOf(Ruling("", "")) + card.ruling).map { (ruling, prevRuling) ->
                            if (ruling.date == prevRuling.date) ruling.text
                            else formatRuling(ruling)
                        }.joinToString("<br><br>")
                )
            }
        }

        if (card.altTitles.isEmpty()) {
            altView.visibility = android.view.View.GONE
        } else {
            altView.text = card.altTitles.joinToString("\n")
            altView.setOnClickListener {
                context.startActivity {
                    val searchOptions = SearchOptions(
                            query = card.title,
                            facets = mapOf(LAYOUT to listOf(card.layout)))
                    CardListActivity.getIntent(context, searchOptions)
                }
            }
        }

        if (card.publications.any { it.foilPrice > 0 }) foilHintView.visible()
        else foilHintView.gone()

        fun setPaperTitle() { showPaperView.text = context.getString(if (showPaper) R.string.paper_price_enabled else R.string.paper_price_disabled) }

        setPaperTitle()
        showPaperView.setOnClickListener {
            callbacks.switchPrice()
            showPaper = !showPaper
            setPaperTitle()
        }

        return view
    }

    private fun formatRuling(ruling: Ruling) = "<b>${ruling.date}</b> — ${ruling.text}"

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