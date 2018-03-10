package fr.gstraymond.android

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckDetailHandAdapter
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.startActivity

class DeckDetailHandFragment : Fragment(), DeckDetailHandAdapter.ClickCallbacks {

    companion object {
        private const val CARDS = "cards"
    }

    private val deckDetailHandAdapter by lazy { DeckDetailHandAdapter(activity, this) }

    private lateinit var recyclerView: RecyclerView
    private val comparator = compareBy<Card>({ it.convertedManaCost }, { it.getLocalizedTitle(context) })

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_deck_detail_hand, container, false)

    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.find<RecyclerView>(R.id.deck_detail_hand_recyclerview).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = deckDetailHandAdapter
        }

        view.find<FloatingActionButton>(R.id.fab).setOnClickListener {
            displayHand()
        }

        savedInstanceState?.getParcelableArrayList<Card>(CARDS)?.apply {
            deckDetailHandAdapter.cards = this
            deckDetailHandAdapter.notifyDataSetChanged()
        } ?:  displayHand()
    }

    private fun displayHand() {
        val deckId = activity.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA)
        val cardList = activity.app().cardListBuilder.build(deckId.toInt())
        val cards = cardList.flatMap { deckCard -> (1..deckCard.counts.deck).map { deckCard.card } }

        if (cards.size < 7) return

        deckDetailHandAdapter.cards = cards.shuffled().take(7).sortedWith(comparator)
        deckDetailHandAdapter.notifyDataSetChanged()
    }

    override fun cardClicked(card: Card) = startActivity {
        CardDetailActivity.getIntent(activity, card)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(CARDS, ArrayList(deckDetailHandAdapter.cards))
        super.onSaveInstanceState(outState)
    }
}