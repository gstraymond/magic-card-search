package fr.gstraymond.android

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.analytics.Tracker
import fr.gstraymond.android.adapter.DeckListAdapter
import fr.gstraymond.biz.DeckManager
import fr.gstraymond.models.Deck
import fr.gstraymond.utils.*

class DeckListFragment : Fragment() {

    private lateinit var deckListAdapter: DeckListAdapter
    private val deckList by lazy { app().deckList }
    private val cardListBuilder by lazy { app().cardListBuilder }
    private val deckManager by lazy { DeckManager(deckList, cardListBuilder) }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        deckListAdapter = DeckListAdapter(context).apply {
            onClickListener = { deckId ->
                View.OnClickListener {
                    startActivity {
                        DeckDetailActivity.getIntent(context, deckId)
                    }
                }
            }
        }
        return inflater.inflate(R.layout.fragment_lists, container, false).apply {
            find<FloatingActionButton>(R.id.lists_fab).let {
                it.visibility = VISIBLE
                it.setOnClickListener {
                    startActivity {
                        Tracker.addRemoveDeck(added = true)
                        val deckId = deckManager.createEmptyDeck()
                        DeckDetailActivity.getIntent(activity, "$deckId")
                    }
                }
            }

            find<RecyclerView>(R.id.lists_recyclerview).let {
                it.layoutManager = LinearLayoutManager(context)
                it.adapter = deckListAdapter
            }

            find<TextView>(R.id.lists_empty_text).setText(R.string.deck_list_empty_text)
        }
    }

    override fun onResume() {
        super.onResume()
        updateDecks()
        if (deckList.isEmpty()) {
            view?.show(R.id.lists_empty_text)
        } else {
            view?.hide(R.id.lists_empty_text)
        }
    }

    private fun updateDecks() {
        deckListAdapter.apply {
            decks = getSortedDecks()
            notifyDataSetChanged()
        }
    }

    private fun getSortedDecks() =
            deckList.all().sortedBy(Deck::timestamp).reversed()
}