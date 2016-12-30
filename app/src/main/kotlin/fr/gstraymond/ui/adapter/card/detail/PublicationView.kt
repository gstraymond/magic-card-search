package fr.gstraymond.ui.adapter.card.detail

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.biz.SetImageGetter
import fr.gstraymond.models.search.response.Publication
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class PublicationView(context: Context) : View<Publication>(context, R.layout.card_set) {
    private val setImageGetter = SetImageGetter(context)
    private val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    override fun getView(item: Publication, view: android.view.View): android.view.View {
        val publicationImage = view.findViewById(R.id.card_textview_set_image) as ImageView
        val publicationImageAlt = view.findViewById(R.id.card_textview_set_image_alt) as TextView
        val publicationText = view.findViewById(R.id.card_textview_set_text) as TextView
        val publicationYear = view.findViewById(R.id.card_textview_set_year) as TextView
        val publicationPrice = view.findViewById(R.id.card_textview_set_price) as TextView
        val setDrawable = setImageGetter.getDrawable(item)

        if (setDrawable == null) {
            publicationImage.visibility = android.view.View.GONE
            publicationImageAlt.visibility = android.view.View.VISIBLE
            publicationImageAlt.text = "?"
        } else {
            publicationImageAlt.visibility = android.view.View.GONE
            publicationImage.visibility = android.view.View.VISIBLE
            publicationImage.setImageDrawable(setDrawable)
        }
        if (item.editionReleaseDate != null) {
            publicationYear.text = dateFormat.format(item.editionReleaseDate)
        } else {
            publicationYear.text = ""
        }
        publicationText.text = item.edition
        val price = formatPrice(item)
        if (price == "") {
            publicationPrice.visibility = android.view.View.GONE
        } else {
            publicationPrice.visibility = android.view.View.VISIBLE
            publicationPrice.text = price
        }

        return view
    }

    private fun formatPrice(publication: Publication) = when (publication.price) {
        0.0 -> ""
        else -> BigDecimal(publication.price)
                .round(MathContext(2, RoundingMode.HALF_EVEN))
                .run { "$" + toPlainString() }
    }
}