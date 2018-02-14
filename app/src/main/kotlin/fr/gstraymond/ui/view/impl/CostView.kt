package fr.gstraymond.ui.view.impl

import android.content.Context
import android.widget.ImageView
import fr.gstraymond.R
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.view.CommonDisplayableView
import fr.gstraymond.utils.color

class CostView(private val context: Context) : CommonDisplayableView<ImageView>(R.id.array_adapter_card_price) {

    private val max1 = 1.0
    private val max2 = 10.0

    override fun display(view: ImageView, card: Card) = card.publications.any { it.price > 0 }

    override fun setValue(view: ImageView, card: Card, position: Int) {
        val prices = card.publications.map { it.price }.filter { it > 0 }

        val color = when {
            prices.any { it <= max1 } -> R.color.colorPrimaryDark
            prices.any { it > max1 && it <= max2 } -> R.color.colorPrimary
            else -> R.color.colorAccent
        }

        view.setColorFilter(context.resources.color(color))
    }
}
