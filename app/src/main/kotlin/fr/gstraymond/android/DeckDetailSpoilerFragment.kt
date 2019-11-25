package fr.gstraymond.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckDetailSpoilerAdapter
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.startActivity

class DeckDetailSpoilerFragment : Fragment(), DeckDetailSpoilerAdapter.ClickCallbacks {

    private val deckDetailHandAdapter by lazy { DeckDetailSpoilerAdapter(activity!!, this) }

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_deck_detail_hand, container, false)

    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // FIXME deck_detail_hand_recyclerview
        recyclerView = view.find<RecyclerView>(R.id.deck_detail_hand_recyclerview).apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 3)
            adapter = deckDetailHandAdapter
        }


        val deckId = activity!!.intent.getStringExtra(DeckDetailActivity.DECK_EXTRA)
        val cardList = activity!!.app().cardListBuilder.build(deckId.toInt())
        deckDetailHandAdapter.pairs = cardList.map { it.card to it.total() } // FIXME total
    }

    override fun cardClicked(card: Card) = startActivity {
        CardDetailActivity.getIntent(activity!!, card)
    }
}