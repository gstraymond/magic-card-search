package fr.gstraymond.android

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckDetailCardsAdapter
import fr.gstraymond.android.adapter.DeckLineCallback
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.constants.Consts
import fr.gstraymond.db.json.CardList
import fr.gstraymond.models.DeckLine
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find

class DeckDetailCardsFragment : Fragment(), DeckLineCallback {

    private val log = Log(javaClass)

    private lateinit var cardList: CardList
    private lateinit var cardTotal: TextView

    var deckLineCallback: DeckLineCallback? = null

    private val deckDetailAdapter by lazy {
        DeckDetailCardsAdapter(activity).apply {
            deckLineCallback = this@DeckDetailCardsFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_deck_detail_cards, container, false).apply {
                find<RecyclerView>(R.id.deck_detail_cards_recyclerview).apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = deckDetailAdapter
                }
                cardTotal = find<TextView>(R.id.deck_detail_cards_total)
            }

    override fun onResume() {
        super.onResume()
        val deckId = activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA)
        cardList = activity.app().cardListBuilder.build(deckId.toInt())
        deckDetailAdapter.let {
            it.cardList = cardList
            it.notifyDataSetChanged()
        }
        updateTotal()
    }

    private fun updateTotal() {
        val deckStats = DeckStats(cardList.all())
        cardTotal.text = "${deckStats.deckSize} cards / sideboard: ${deckStats.sideboardSize}"
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
        val intent = Intent(activity, CardDetailActivity::class.java).apply {
            putExtra(Consts.CARD, deckLine.card)
        }
        activity.startActivity(intent)
        deckLineCallback?.cardClick(deckLine)
    }
}