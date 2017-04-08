package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import fr.gstraymond.R
import fr.gstraymond.analytics.Tracker
import fr.gstraymond.android.adapter.DeckListAdapter
import fr.gstraymond.biz.DeckManager
import fr.gstraymond.models.Deck
import fr.gstraymond.utils.*

class DecksActivity : CustomActivity(R.layout.activity_decks) {

    private val deckListAdapter by lazy {
        DeckListAdapter(this).apply {
            onClickListener = { deckId ->
                View.OnClickListener {
                    startActivity {
                        DeckDetailActivity.getIntent(this@DecksActivity, deckId)
                    }
                }
            }
        }
    }
    private val deckList by lazy { app().deckList }
    private val cardListBuilder by lazy { app().cardListBuilder }
    private val deckManager by lazy { DeckManager(deckList, cardListBuilder) }

    companion object {
        fun getIntent(context: Context) = Intent(context, DecksActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.decks_title)

        find<FloatingActionButton>(R.id.decks_fab).let {
            it.setOnClickListener {
                startActivity {
                    Tracker.addRemoveDeck(added = true)
                    val deckId = deckManager.createEmptyDeck()
                    DeckDetailActivity.getIntent(this, "$deckId")
                }
            }
        }

        find<RecyclerView>(R.id.decks_recyclerview).let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = deckListAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        updateDecks()
        if (deckList.isEmpty()) {
            show(R.id.decks_empty_text)
        } else {
            hide(R.id.decks_empty_text)
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