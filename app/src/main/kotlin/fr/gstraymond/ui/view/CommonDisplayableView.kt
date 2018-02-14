package fr.gstraymond.ui.view

import android.view.View

import fr.gstraymond.api.ui.view.DisplayableView
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.gone
import fr.gstraymond.utils.visible

abstract class CommonDisplayableView<in V : View>(override val id: Int) : DisplayableView {

    abstract fun display(view: V, card: Card): Boolean

    abstract fun setValue(view: V, card: Card, position: Int)

    override fun display(parentView: View, card: Card, position: Int) {
        val view = parentView.findViewById<V>(id)
        if (display(view, card)) {
            view.visible()
            setValue(view, card, position)
        } else {
            view.gone()
        }
    }
}
