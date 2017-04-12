package fr.gstraymond.android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.clans.fab.FloatingActionButton
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckDetailCardsAdapter
import fr.gstraymond.android.adapter.DeckLineCallback
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.db.json.CardList
import fr.gstraymond.models.DeckLine
import fr.gstraymond.utils.*

class DeckDetailCardsFragment : Fragment(), DeckLineCallback {

    private val log = Log(javaClass)

    private lateinit var cardList: CardList
    private lateinit var cardTotal: TextView
    private lateinit var frame: View
    private lateinit var emptyText: TextView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabHistory: FloatingActionButton

    var deckLineCallback: DeckLineCallback? = null

    private val deckDetailAdapter by lazy {
        DeckDetailCardsAdapter(activity).apply {
            deckLineCallback = this@DeckDetailCardsFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_deck_detail_cards, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.find<RecyclerView>(R.id.deck_detail_cards_recyclerview).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deckDetailAdapter
        }
        cardTotal = view.find(R.id.deck_detail_cards_total)
        frame = view.find(R.id.deck_detail_cards_frame)
        emptyText = view.find(R.id.deck_detail_cards_empty)
        fabAdd = view.find(R.id.deck_detail_cards_add)
        fabHistory = view.find(R.id.deck_detail_cards_add_history)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val deckId = activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA)

        fabAdd.apply {
            setOnClickListener {
                startActivity {
                    CardListActivity.getIntent(activity, SearchOptions(deckId = deckId, size = 0))
                }
            }
        }

        fabHistory.apply {
            setOnClickListener {
                startActivity {
                    HistoryActivity.getIntent(activity, deckId)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val deckId = activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA)
        cardList = activity.app().cardListBuilder.build(deckId.toInt())
        updateTotal()
        deckDetailAdapter.let {
            it.cardList = cardList
            it.notifyDataSetChanged()
        }
    }

    private fun updateTotal() {
        if (cardList.isEmpty()) {
            frame.hide()
            emptyText.show()
        } else {
            frame.show()
            emptyText.hide()
            val deckStats = DeckStats(cardList.all())
            // FIXME translate
            cardTotal.text = "${deckStats.deckSize} cards / sideboard: ${deckStats.sideboardSize}"
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
}