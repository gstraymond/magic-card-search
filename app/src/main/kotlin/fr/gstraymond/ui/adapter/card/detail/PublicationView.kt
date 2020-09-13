package fr.gstraymond.ui.adapter.card.detail

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.prefs
import fr.gstraymond.biz.SetImageGetter
import fr.gstraymond.models.search.response.Publication
import fr.gstraymond.utils.find
import fr.gstraymond.utils.gone
import fr.gstraymond.utils.visible
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class PublicationView(context: Context) : View<Publication>(context, R.layout.card_set) {

    private val setImageGetter = SetImageGetter(context)
    private val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    var showPaper = prefs.paperPrice

    override fun getView(card: Publication, view: android.view.View): android.view.View {
        val publicationImage = view.find<ImageView>(R.id.card_textview_set_image)
        val publicationImageAlt = view.find<TextView>(R.id.card_textview_set_image_alt)
        val publicationText = view.find<TextView>(R.id.card_textview_set_text)
        val publicationYear = view.find<TextView>(R.id.card_textview_set_year)
        val publicationPrice = view.find<TextView>(R.id.card_textview_set_price)
        val setDrawable = setImageGetter.getDrawable(card)

        if (setDrawable == null) {
            publicationImage.gone()
            publicationImageAlt.visible()
            publicationImageAlt.text = "?"
        } else {
            publicationImageAlt.gone()
            publicationImage.visible()
            publicationImage.setImageDrawable(setDrawable)
        }
        if (card.editionReleaseDate != null) {
            publicationYear.text = dateFormat.format(card.editionReleaseDate)
        } else {
            publicationYear.text = ""
        }
        publicationText.text = card.edition
        val price = formatPrice(if (showPaper) card.price else card.mtgoPrice)
        val foilPrice = formatPrice(if (showPaper) card.foilPrice else card.mtgoFoilPrice, foil = true)
        if (price == "" && foilPrice == "") {
            publicationPrice.gone()
        } else {
            publicationPrice.visible()
            publicationPrice.text = sequenceOf(price, foilPrice).filter { it != "" }.joinToString("\n")
        }

        return view
    }

    private fun formatPrice(price: Double,
                            foil: Boolean = false) = when (price) {
        0.0 -> ""
        in 0.0..0.1 -> formatPrice(BigDecimal(price).setScale(2, RoundingMode.HALF_EVEN), foil)
        else -> formatPrice(BigDecimal(price).round(MathContext(2, RoundingMode.HALF_EVEN)), foil)
    }

    private fun formatPrice(bd: BigDecimal,
                            foil: Boolean = false) = bd.run { "$" + toPlainString() + (if (foil) " *" else "") }
}