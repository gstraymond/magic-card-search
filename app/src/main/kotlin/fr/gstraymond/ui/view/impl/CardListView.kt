package fr.gstraymond.ui.view.impl

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatButton
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.CardClickCallbacks
import fr.gstraymond.ui.view.CommonDisplayableView

abstract class CardListView<out A>(private val cards: JsonList<A>,
                                   private val clickCallbacks: CardClickCallbacks,
                                   private val colorEnabled: ColorStateList,
                                   private val colorDisabled: ColorStateList,
                                   private val iconEnabled: Drawable,
                                   private val iconDisabled: Drawable,
                                   private val listName: String) : CommonDisplayableView<AppCompatButton>(R.id.card_favorite) {

    private val log = Log(javaClass)

    abstract fun getElem(card: Card): A

    override fun display(view: AppCompatButton, card: Card) = true

    override fun setValue(view: AppCompatButton, card: Card, position: Int) {
        view.setOnClickListener {
            if (cards.addOrRemove(getElem(card))) {
                clickCallbacks.itemAdded(position)
            } else {
                clickCallbacks.itemRemoved(position)
            }
            updateState(card, view)
        }

        updateState(card, view)
    }

    private fun updateState(card: Card, view: AppCompatButton) {
        val contains = cards.contains(getElem(card))
        log.d("contains $card -> $contains [${view.javaClass}]")
        if (contains) {
            view.setCompoundDrawablesWithIntrinsicBounds(iconEnabled, null, null, null)
            view.supportBackgroundTintList = colorEnabled
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(iconDisabled, null, null, null)
            view.supportBackgroundTintList = colorDisabled
        }
    }
}