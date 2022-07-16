package fr.gstraymond.ui.view.impl

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import fr.gstraymond.R
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.android.adapter.DeckCardCallback
import fr.gstraymond.biz.Formats
import fr.gstraymond.models.Board
import fr.gstraymond.models.Board.*
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.SimpleCardViews
import fr.gstraymond.ui.view.CommonDisplayableView
import fr.gstraymond.utils.*

class QuantityView(private val context: Context,
                   private val app: CustomApplication,
                   private val deckId: Int,
                   private val board: Board,
                   private val deckCardCallback: DeckCardCallback?,
                   private val deckEditor: Boolean) : CommonDisplayableView<AppCompatButton>(R.id.array_adapter_deck_card_mult) {

    private val cardViews = SimpleCardViews()

    override fun setValue(view: AppCompatButton, card: Card, position: Int) {
        val cardList = app.cardListBuilder.build(deckId)
        val deckCard = cardList.getByUid(card.getId())
        deckCard?.apply {
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            view.supportBackgroundTintList = context.resources.colorStateList(
                    if (deckEditor) R.color.colorPrimaryDark else R.color.colorAccent
            )
            view.text = "${getMult(this)}"
        } ?: {
            view.setCompoundDrawablesWithIntrinsicBounds(context.resources.drawable(R.drawable.ic_bookmark_border_white_24dp), null, null, null)
            view.supportBackgroundTintList = context.resources.colorStateList(R.color.colorPrimaryDark)
            view.text = null
        }()

        view.setOnClickListener {
            val multView = context.inflate(R.layout.array_adapter_deck_card_mult)
            cardViews.display(multView, card, position)

            val isCommander = app.deckList.getByUid("$deckId")!!.isCommander()
            val sideboardView = multView.find<View>(R.id.array_adapter_deck_sideboard)
            if (isCommander) sideboardView.gone()
            val commanderView = multView.find<View>(R.id.array_adapter_deck_commander)
            if (!isCommander) commanderView.gone()

            val deckCount = multView.find<TextView>(R.id.array_adapter_deck_card_mult).apply {
                text = deckCard?.counts?.deck?.toString() ?: "0"
            }
            val sbCount = multView.find<TextView>(R.id.array_adapter_deck_sb_mult).apply {
                text = deckCard?.counts?.sideboard?.toString() ?: "0"
            }
            val mbCount = multView.find<TextView>(R.id.array_adapter_deck_mb_mult).apply {
                text = deckCard?.counts?.maybe?.toString() ?: "0"
            }

            val maxOccurrence = FormatValidator.getMaxOccurrence(card, app.deckList.getByUid("$deckId")?.format())

            val megamap: Map<String, Map<String, View>> = listOf("card", "sb", "mb").map { line ->
                line to (listOf("add", "remove").map {
                    it to multView.find<AppCompatButton>(getId("array_adapter_deck_${line}_${it}_1"))
                }.toMap() + mapOf("mult" to multView.find<TextView>(getId("array_adapter_deck_${line}_mult"))))
            }.toMap()

            megamap.forEach { (line, buttonMap) ->
                val maxOccForLine = if (line == "mb") 99 else maxOccurrence
                val multTextView = buttonMap["mult"] as TextView
                val otherMap = megamap[megamap.keys.filterNot { it == line }.first()]!!
                val otherMultView = otherMap["mult"] as TextView
                updateVisibility(multTextView.text.toString().toInt(), otherMultView.text.toString().toInt(), buttonMap, maxOccForLine)
                buttonMap.filter { it.value is AppCompatButton }.forEach { (action, button) ->
                    val coef = if (action == "add") 1 else -1
                    (button as AppCompatButton).apply {
                        supportBackgroundTintList = context.resources.colorStateList(R.color.colorPrimary)
                        setOnClickListener {
                            val currentMult = multTextView.text.toString().toInt()
                            val newMult = currentMult + coef
                            multTextView.text = newMult.toString()
                            val otherMult = otherMultView.text.toString().toInt()
                            updateVisibility(newMult, otherMult, buttonMap, maxOccForLine)
                            if (line != "mb") {
                                updateVisibility(otherMult, newMult, otherMap, maxOccForLine)
                            }
                        }
                    }
                }
            }

            AlertDialog.Builder(context)
                    .setView(multView)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        val pickerDeckMult = deckCount.text.toString().toInt()
                        val pickerSbMult = sbCount.text.toString().toInt()
                        val pickerMbMult = mbCount.text.toString().toInt()

                        deckCard?.apply {
                            val updatedDeckCard = setDeckCount(pickerDeckMult).setSBCount(pickerSbMult).setMaybeCount(pickerMbMult)
                            when (updatedDeckCard.total()) {
                                0 -> cardList.delete(updatedDeckCard)
                                else -> cardList.update(updatedDeckCard)
                            }
                        } ?: {
                            val newDeckCard = DeckCard(card).setDeckCount(pickerDeckMult).setSBCount(pickerSbMult).setMaybeCount(pickerMbMult)
                            if (newDeckCard.total() > 0) {
                                cardList.addOrRemove(newDeckCard)
                            }
                        }()
                        deckCardCallback?.multChanged(board, position)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
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

    private fun getMult(deckCard: DeckCard) = when (board) {
        DECK -> deckCard.counts.deck
        SB -> deckCard.counts.sideboard
        MAYBE -> deckCard.counts.maybe
    }

    override fun display(view: AppCompatButton, card: Card) = true
}