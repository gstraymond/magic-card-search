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
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckDetailCardsAdapter
import fr.gstraymond.android.adapter.DeckLineCallback
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.constants.FacetConst.FORMAT
import fr.gstraymond.constants.FacetConst.TYPE
import fr.gstraymond.db.json.CardList
import fr.gstraymond.models.Deck
import fr.gstraymond.models.DeckLine
import fr.gstraymond.ocr.OcrCaptureActivity
import fr.gstraymond.utils.*

class DeckDetailCardsFragment : Fragment(), DeckLineCallback {

    private val log = Log(javaClass)

    private val REQUEST_CAMERA_CODE = 1233

    private lateinit var cardList: CardList
    private lateinit var cardTotal: TextView
    private lateinit var frame: View
    private lateinit var emptyText: TextView
    private lateinit var floatingMenu: FloatingActionMenu
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabHistory: FloatingActionButton
    private lateinit var fabLand: FloatingActionButton
    private lateinit var fabScan: FloatingActionButton
    private lateinit var notImported: TextView

    var deckLineCallback: DeckLineCallback? = null

    private val deckDetailAdapter by lazy {
        DeckDetailCardsAdapter(activity).apply {
            deckLineCallback = this@DeckDetailCardsFragment
        }
    }

    private val deckList by lazy { activity.app().deckList }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_deck_detail_cards, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.find<RecyclerView>(R.id.deck_detail_cards_recyclerview).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = deckDetailAdapter
        }
        cardTotal = view.find(R.id.deck_detail_cards_total)
        frame = view.find(R.id.deck_detail_cards_frame)
        emptyText = view.find(R.id.deck_detail_cards_empty)
        floatingMenu = view.find(R.id.deck_detail_cards_floating_menu)
        fabAdd = view.find(R.id.deck_detail_cards_add)
        fabHistory = view.find(R.id.deck_detail_cards_add_history)
        fabLand = view.find(R.id.deck_detail_cards_add_land)
        fabScan = view.find(R.id.deck_detail_cards_camera_scan)
        notImported = view.find(R.id.deck_detail_cards_not_imported)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val deckId = activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA)

        fabAdd.setOnClickListener {
            startActivity {
                CardListActivity.getIntent(activity, SearchOptions(deckId = deckId, size = 0))
            }
        }

        fabHistory.setOnClickListener {
            startActivity {
                HistoryActivity.getIntent(activity, deckId)
            }
        }

        fabLand.setOnClickListener {
            startActivity {
                val facets = mapOf(TYPE to listOf("land", "basic"), FORMAT to listOf("Standard"))
                CardListActivity.getIntent(activity, SearchOptions(deckId = deckId, facets = facets))
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
                    deckId = activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA))
        }
    }

    override fun onResume() {
        super.onResume()
        floatingMenu.close(false)
        val deckId = activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA)
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

        cardList = activity.app().cardListBuilder.build(deckId.toInt())
        updateTotal()
        deckDetailAdapter.let {
            it.cardList = cardList
            it.notifyDataSetChanged()
        }
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
        if (cardList.isEmpty()) {
            frame.gone()
            emptyText.visible()
        } else {
            frame.visible()
            emptyText.gone()
            val deckStats = DeckStats(cardList.all())
            cardTotal.text = "${deckStats.deckSize} / ${deckStats.sideboardSize}"
        }
    }

    override fun multChanged(deckLine: DeckLine, mult: Int) {
        log.d("multChanged: [$mult] $deckLine")
        when (mult) {
            0 -> cardList.delete(deckLine)
            else -> cardList.update(deckLine.copy(mult = mult))
        }
        updateTotal()
        deckLineCallback?.multChanged(deckLine, mult)
    }

    override fun sideboardChanged(deckLine: DeckLine, sideboard: Boolean) {
        log.d("sideboardChanged: [$sideboard] $deckLine")
        cardList.update(deckLine.copy(isSideboard = sideboard))
        updateTotal()
        deckLineCallback?.sideboardChanged(deckLine, sideboard)
    }

    override fun cardClick(deckLine: DeckLine) {
        startActivity {
            CardDetailActivity.getIntent(context, deckLine.card)
        }
        deckLineCallback?.cardClick(deckLine)
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
}