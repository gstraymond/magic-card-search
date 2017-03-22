package fr.gstraymond.api.ui.view

import android.view.View
import fr.gstraymond.models.search.response.Card

interface DisplayableView {

    val id: Int

    fun display(parentView: View, card: Card, position: Int)
}
