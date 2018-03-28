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
import fr.gstraymond.android.adapter.DeckCardCallback.FROM
import fr.gstraymond.android.adapter.DeckDetailCardsAdapter
import fr.gstraymond.biz.Formats.BRAWL
import fr.gstraymond.biz.Formats.COMMANDER
import fr.gstraymond.biz.Formats.STANDARD
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

    private var viewCreated = false

    private val deckId by lazy { activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA) }

    var deckCardCallback: DeckCardCallback? = null
    var sideboard: Boolean = false

    private val deckDetailAdapter by lazy {
        DeckDetailCardsAdapter(app(), activity, sideboard, deckId.toInt()).apply {
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

        viewCreated = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fabAdd.setOnClickListener {
            startActivity {
                val searchOptions = deckList.getByUid(deckId)?.maybeFormat?.run {
                    SearchOptions(facets = mapOf(FacetConst.FORMAT to listOf(getFormat())), deckId = deckId, addToSideboard = sideboard)
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
                startScanner()
            }
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

    private fun updateTotal() {
        if (!viewCreated) return

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

                val (targetDeckSize, deckSize) = when (this) {
                    COMMANDER -> SpecificSize(100) to deck.deckSize + deck.sideboardSize
                    BRAWL -> SpecificSize(60) to deck.deckSize + deck.sideboardSize
                    else -> MinSize(60) to deck.deckSize
                }

                when (targetDeckSize) {
                    is MinSize -> {
                        val size = targetDeckSize.size
                        if (deckSize < size)
                            msgs += getText(R.string.validation_missing_cards, "${size - deckSize}", "$size")
                    }
                    is SpecificSize -> {
                        val size = targetDeckSize.size
                        if (deckSize != size)
                            msgs += getText(R.string.validation_specific_cards, "$size")
                    }
                }

                cardList.filter { it.counts.deck > 1 }
                        .map { it to FormatValidator.getMaxOccurrence(it.card, this) }
                        .filter { it.first.counts.deck > it.second }
                        .forEach { msgs += getText(R.string.validation_max_occurrence, it.first.card.getLocalizedTitle(context), "${it.second}") }

                cardList.filter { !it.card.formats.contains(getFormat()) }
                        .forEach { msgs += getText(R.string.validation_bad_format, it.card.getLocalizedTitle(context), this) }

                if (msgs.isEmpty()) {
                    formatProblems.gone()
                } else {
                    formatProblems.visible()
                    formatProblems.text = msgs.joinToString("\n")
                }
            } ?: formatProblems.gone()
        }
    }

    private fun String.getFormat() = if (equals(BRAWL)) STANDARD else this

    private fun getText(textId: Int, vararg args: String) =
            String.format(resources.getString(textId), *args)

    override fun multChanged(from: DeckCardCallback.FROM, position: Int) {
        if (!viewCreated) return
        updateTotal()
        deckDetailAdapter.updateDeckList()
        if (FROM.DECK == from && !sideboard || FROM.SB == from && sideboard)
            deckCardCallback?.multChanged(from, position)
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

sealed class TargetDeckSize

data class SpecificSize(val size: Int) : TargetDeckSize()
data class MinSize(val size: Int) : TargetDeckSize()
