package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckDetailAdapter
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.models.CardWithOccurrence
import java.util.*

class DeckDetailActivity : CustomActivity(R.layout.activity_deck_detail) {

    companion object {
        val DECK_EXTRA = "deck"

        fun getIntent(context: Context, deckId: String) =
                Intent(context, DeckDetailActivity::class.java).apply {
                    putExtra(DECK_EXTRA, deckId)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deckId = intent.getStringExtra(DECK_EXTRA)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = customApplication.deckList.getByUid(deckId)?.name
        }

        val deck = customApplication.cardListBuilder.build(deckId.toInt())
        val cards = deck.all()

        val recyclerView = findViewById(R.id.deck_recyclerview) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DeckDetailAdapter(cards.sortedWith(Comparator<CardWithOccurrence> { c1, c2 ->
            val z1 = if (c1.isSideboard) 1000 else -1000
            val z2 = if (c2.isSideboard) -1000 else 1000
            z1 + z2 + c1.card.title.compareTo(c2.card.title)
        }))

        val deckStats = DeckStats(cards)

        val textView = findViewById(R.id.deck_stats) as TextView
        textView.text = """
            colors: ${deckStats.colors.joinToString()}
            formats: ${deckStats.format}
            cards: ${deckStats.mainDeck.map { it.occurrence }.sum()}
            sidebard: ${deckStats.sideboard.map { it.occurrence }.sum()}
        """
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.deck_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.deckdetails_add -> {
            startActivity {
                CardListActivity.getIntent(this, SearchOptions(deckId = intent.getStringExtra(DECK_EXTRA)))
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
