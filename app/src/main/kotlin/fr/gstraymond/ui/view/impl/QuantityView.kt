package fr.gstraymond.ui.view.impl

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.view.View
import android.widget.TextView
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.android.adapter.DeckCardCallback
import fr.gstraymond.android.adapter.DeckCardCallback.FROM
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.SimpleCardViews
import fr.gstraymond.ui.view.CommonDisplayableView
import fr.gstraymond.utils.*

class QuantityView(private val context: Context,
                   private val app: CustomApplication,
                   private val deckId: Int,
                   private val sideboard: Boolean,
                   private val deckCardCallback: DeckCardCallback?) : CommonDisplayableView<AppCompatButton>(R.id.array_adapter_deck_card_mult) {

    private val cardViews = SimpleCardViews()

    init {
        Log(javaClass).w("sideboard: $sideboard")
    }

    override fun setValue(view: AppCompatButton, card: Card, position: Int) {
        val cardList = app.cardListBuilder.build(deckId)
        val deckCard = cardList.getByUid(card.getId())
        deckCard?.apply {
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            view.supportBackgroundTintList = context.resources.colorStateList(R.color.colorAccent)
            view.text = "${getMult(this)}"
        } ?: {
            view.setCompoundDrawablesWithIntrinsicBounds(context.resources.drawable(R.drawable.ic_bookmark_border_white_18dp), null, null, null)
            view.supportBackgroundTintList = context.resources.colorStateList(R.color.colorPrimaryDark)
            view.text = null
        }()

        view.setOnClickListener {
            val view = context.inflate(R.layout.array_adapter_deck_card_mult)
            cardViews.display(view, card, position)
            val deckCount = view.find<TextView>(R.id.array_adapter_deck_card_mult).apply {
                text = deckCard?.counts?.deck?.toString() ?: "0"
            }
            val sbCount = view.find<TextView>(R.id.array_adapter_deck_sb_mult).apply {
                text = deckCard?.counts?.sideboard?.toString() ?: "0"
            }

            val maxOccurrence = FormatValidator.getMaxOccurrence(card, app.deckList.getByUid("$deckId")?.maybeFormat)

            val megamap: Map<String, Map<String, View>> = listOf("card", "sb").map { line ->
                line to (listOf("add", "remove").map {
                    it to view.find<AppCompatButton>(getId("array_adapter_deck_${line}_${it}_1"))
                }.toMap() + mapOf("mult" to view.find<TextView>(getId("array_adapter_deck_${line}_mult"))))
            }.toMap()

            megamap.forEach { (line, buttonMap) ->
                val multView = buttonMap["mult"] as TextView
                val otherMap = megamap[megamap.keys.filterNot { it == line }.first()]!!
                val otherMultView = otherMap["mult"] as TextView
                updateVisibility(multView.text.toString().toInt(), otherMultView.text.toString().toInt(), buttonMap, maxOccurrence)
                buttonMap.filter { it.value is AppCompatButton }.forEach { (action, button) ->
                    val coef = if (action == "add") 1 else -1
                    (button as AppCompatButton).apply {
                        supportBackgroundTintList = context.resources.colorStateList(R.color.colorPrimary)
                        setOnClickListener {
                            val currentMult = multView.text.toString().toInt()
                            val newMult = currentMult + coef
                            multView.text = newMult.toString()
                            val otherMult = otherMultView.text.toString().toInt()
                            updateVisibility(newMult, otherMult, buttonMap, maxOccurrence)
                            updateVisibility(otherMult, newMult, otherMap, maxOccurrence)
                        }
                    }
                }
            }

            AlertDialog.Builder(context)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                        val pickerDeckMult = deckCount.text.toString().toInt()
                        val pickerSbMult = sbCount.text.toString().toInt()

                        deckCard?.apply {
                            val updatedDeckCard = setDeckCount(pickerDeckMult).setSBCount(pickerSbMult)
                            when (updatedDeckCard.total()) {
                                0 -> cardList.delete(updatedDeckCard)
                                else -> cardList.update(updatedDeckCard)
                            }
                        } ?: {
                            val newDeckCard = DeckCard(card).setDeckCount(pickerDeckMult).setSBCount(pickerSbMult)
                            if (newDeckCard.total() > 0) {
                                cardList.addOrRemove(newDeckCard)
                            }
                        }()
                        deckCardCallback?.multChanged(if (sideboard) FROM.SB else FROM.DECK, position)
                    })
                    .setNegativeButton(android.R.string.cancel, { _, _ -> })
                    .create()
                    .show()
        }
    }

    private fun getId(id: String) =
            context.resources.getIdentifier(id, "id", context.packageName)

    private fun updateVisibility(newMult: Int,
                                 otherMult: Int,
                                 buttonMap: Map<String, View>,
                                 maxOccurrence: Int) {
        buttonMap["remove"]?.apply {
            if (newMult == 0) invisible() else visible()
        }

        buttonMap["add"]?.apply {
            if (newMult + otherMult >= maxOccurrence) invisible() else visible()
        }
    }

    private fun getMult(deckCard: DeckCard) =
            if (sideboard) deckCard.counts.sideboard
            else deckCard.counts.deck

    override fun display(view: AppCompatButton, card: Card) = true
}