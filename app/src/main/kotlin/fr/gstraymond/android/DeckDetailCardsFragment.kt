package fr.gstraymond.android

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckCardCallback
import fr.gstraymond.android.adapter.DeckCardCallback.FROM.DECK
import fr.gstraymond.android.adapter.DeckCardCallback.FROM.SB
import fr.gstraymond.android.adapter.DeckDetailCardsAdapter
import fr.gstraymond.biz.Formats
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.constants.FacetConst
import fr.gstraymond.constants.FacetConst.FORMAT
import fr.gstraymond.constants.FacetConst.TYPE
import fr.gstraymond.db.json.DeckCardList
import fr.gstraymond.models.Deck
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.ocr.OcrCaptureActivity
import fr.gstraymond.utils.*

class DeckDetailCardsFragment : Fragment(), DeckCardCallback, DeckDetailActivity.FormatCallback {

    private val REQUEST_CAMERA_CODE = 1233

    private lateinit var recyclerView: RecyclerView
    private lateinit var cardList: DeckCardList
    private lateinit var emptyText: TextView
    private lateinit var floatingMenu: FloatingActionMenu
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabHistory: FloatingActionButton
    private lateinit var fabLand: FloatingActionButton
    private lateinit var fabScan: FloatingActionButton
    private lateinit var notImported: TextView
    private lateinit var formatProblems: TextView

    private val deckId by lazy { activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA) }

    var deckCardCallback: DeckCardCallback? = null
    var sideboard: Boolean = false

    private val deckDetailAdapter by lazy {
        DeckDetailCardsAdapter(app(), activity, sideboard).apply {
            deckCardCallback = this@DeckDetailCardsFragment
        }
    }

    private val deckList by lazy { activity.app().deckList }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_deck_detail_cards, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.find<RecyclerView>(R.id.deck_detail_cards_recyclerview).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = deckDetailAdapter
        }
        emptyText = view.find(R.id.deck_detail_cards_empty)
        floatingMenu = view.find(R.id.deck_detail_cards_floating_menu)
        fabAdd = view.find(R.id.deck_detail_cards_add)
        fabHistory = view.find(R.id.deck_detail_cards_add_history)
        fabLand = view.find(R.id.deck_detail_cards_add_land)
        fabScan = view.find(R.id.deck_detail_cards_camera_scan)
        notImported = view.find(R.id.deck_detail_cards_not_imported)
        formatProblems = view.find(R.id.deck_detail_cards_format_problems)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fabAdd.setOnClickListener {
            startActivity {
                val searchOptions = deckList.getByUid(deckId)?.maybeFormat?.run {
                    SearchOptions(facets = mapOf(FacetConst.FORMAT to listOf(this)), deckId = deckId)
                } ?: {
                    SearchOptions.START_SEARCH_OPTIONS().copy(deckId = deckId)
                }()
                CardListActivity.getIntent(activity, searchOptions)
            }
        }

        fabHistory.setOnClickListener {
            startActivity {
                HistoryActivity.getIntent(activity, deckId, sideboard)
            }
        }

        if (sideboard) {
            fabLand.gone()
        } else {
            fabLand.visible()
            fabLand.setOnClickListener {
                startActivity {
                    val facets = mapOf(TYPE to listOf("land", "basic"), FORMAT to listOf("Standard"))
                    CardListActivity.getIntent(activity, SearchOptions(deckId = deckId, facets = facets, addToSideboard = sideboard))
                }
            }
        }

        fabScan.setOnClickListener {
            if (!activity.hasPerms(Manifest.permission.CAMERA)) {
                activity.requestPerms(REQUEST_CAMERA_CODE, Manifest.permission.CAMERA)
            } else {
                // TODO handle sideboard
                startScanner()
            }
        }

        deckDetailAdapter.let {
            it.deckId = deckId.toInt()
        }
    }

    private fun startScanner() {
        startActivity {
            OcrCaptureActivity.getIntent(
                    activity,
                    autoFocus = true,
                    useFlash = false,
                    deckId = deckId,
                    addToSideboard = sideboard)
        }
    }

    override fun onResume() {
        super.onResume()
        cardList = activity.app().cardListBuilder.build(deckId.toInt())

        floatingMenu.close(false)
        val deck = deckList.getByUid(deckId)

        val cardsNotImported = deck?.cardsNotImported ?: listOf()
        if (cardsNotImported.isEmpty()) notImported.gone()
        else {
            val cards = cardsNotImported.map {
                val sideboard = when (it.isSideboard) {
                    true -> "sideboard"
                    else -> "deck"
                }
                """- ${it.mult} x "${it.card}" - $sideboard"""
            }.joinToString("<br>")

            notImported.text = Html.fromHtml("<b>${getString(R.string.deck_detail_cards_not_imported_text)}</b><br>$cards")
            notImported.visible()
            notImported.setOnClickListener {
                deck?.run { createNotImportedDialog(deck) }
            }
        }

        updateTotal()
        deckDetailAdapter.updateDeckList()
    }

    private fun createNotImportedDialog(deck: Deck) {
        AlertDialog.Builder(activity)
                .setTitle(getString(R.string.deck_detail_cards_not_imported_title))
                .setPositiveButton(getString(R.string.deck_detail_cards_not_imported_ok)) { _, _ ->
                    deckList.update(deck.copy(cardsNotImported = listOf()))
                    notImported.gone()
                }
                .setNegativeButton(getString(R.string.deck_detail_cards_not_imported_cancel)) { _, _ -> }
                .show()
    }

    private val cardWithNoSizeRestriction = "A deck can have any number of cards named"

    private fun updateTotal() {
        formatProblems.gone()
        if (cardList.isEmpty()) {
            recyclerView.gone()
            emptyText.visible()
        } else {
            recyclerView.visible()
            emptyText.gone()
            val deck = app().deckList.getByUid(deckId)
            deck?.maybeFormat?.apply {
                val msgs = mutableListOf<String>()

                val (minDeckSize, deckSize, maxCardOccurrence) = when (this) {
                    Formats.COMMANDER -> Triple(100, deck.deckSize + deck.sideboardSize, 1)
                    else -> Triple(60, deck.deckSize, 4)
                }

                if (deckSize < minDeckSize) {
                    msgs += getText(R.string.validation_missing_cards, "${minDeckSize - deckSize}", "$minDeckSize")
                }

                cardList.filter { it.counts.deck > maxCardOccurrence }
                        .filterNot { it.card.type.startsWith("Basic Land") }
                        .filterNot { it.card.description.contains(cardWithNoSizeRestriction, true) }
                        .forEach { msgs += getText(R.string.validation_max_occurrence, it.card.getLocalizedTitle(context),  "$maxCardOccurrence") }

                cardList.filter { !it.card.formats.contains(this) }
                        .forEach { msgs += getText(R.string.validation_bad_format, it.card.getLocalizedTitle(context), this)}

                if (this == Formats.VINTAGE) {
                    cardList.filter { it.card.formats.contains("Restricted") }
                            .filter { it.counts.deck > 1 }
                            .forEach { msgs += getText(R.string.validation_max_occurrence, it.card.getLocalizedTitle(context),  "1") }
                }

                if (msgs.isEmpty()) {
                    formatProblems.gone()
                } else {
                    formatProblems.visible()
                    formatProblems.text = msgs.joinToString("\n")
                }
            } ?: formatProblems.gone()
        }
    }

    private fun getText(textId: Int, vararg args: String) =
            String.format(resources.getString(textId), *args)

    override fun multChanged(deckCard: DeckCard,
                             from: DeckCardCallback.FROM,
                             deck: Int,
                             sideboard: Int) {
        if (from == SB && this@DeckDetailCardsFragment.sideboard ||
                from == DECK && !this@DeckDetailCardsFragment.sideboard) {
            val updatedDeckCard = deckCard.setDeckCount(deck).setSBCount(sideboard)
            when (updatedDeckCard.total()) {
                0 -> cardList.delete(updatedDeckCard)
                else -> cardList.update(updatedDeckCard)
            }
            updateTotal()
            deckCardCallback?.multChanged(deckCard, from, deck, sideboard)
        }
        deckDetailAdapter.updateDeckList()
    }

    override fun cardClick(deckCard: DeckCard) {
        startActivity {
            CardDetailActivity.getIntent(context, deckCard.card)
        }
        deckCardCallback?.cardClick(deckCard)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_CODE -> if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startScanner()
            }
        }
    }

    override fun formatChanged() {
        updateTotal()
    }
}