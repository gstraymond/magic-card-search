package fr.gstraymond.ui.view

import android.view.View

import fr.gstraymond.api.ui.view.DisplayableView
import fr.gstraymond.models.search.response.Card

abstract class CommonDisplayableView<in V : View>(override val id: Int) : DisplayableView {

    abstract fun display(view: V, card: Card): Boolean

    abstract fun setValue(view: V, card: Card, position: Int)

    override fun display(parentView: View, card: Card, position: Int) {
        val view = parentView.findViewById(id) as V
        if (display(view, card)) {
            setValue(view, card, position)
        }
    }

    protected fun display(view: V, display: Boolean) = display.apply {
        view.visibility = when (this) {
            true -> View.VISIBLE
            else -> View.GONE
        }
    }
}
