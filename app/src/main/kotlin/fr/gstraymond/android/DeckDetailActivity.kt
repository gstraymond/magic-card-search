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
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckLineCallback
import fr.gstraymond.android.adapter.DeckDetailAdapter
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.Deck
import fr.gstraymond.utils.find
import java.util.*

class DeckDetailActivity : CustomActivity(R.layout.activity_deck_detail) {

    companion object {
        val DECK_EXTRA = "deck"

        fun getIntent(context: Context, deckId: String) =
                Intent(context, DeckDetailActivity::class.java).apply {
                    putExtra(DECK_EXTRA, deckId)
                }
    }

    lateinit var deck: Deck

    private val log = Log(javaClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deckId = intent.getStringExtra(DECK_EXTRA)
        deck = customApplication.deckList.getByUid(deckId)!!

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = deck.name
        }
    }

    private val cardComparator = Comparator<DeckLine> { c1, c2 ->
        val z1 = if (c1.isSideboard) 1000 else -1000
        val z2 = if (c2.isSideboard) -1000 else 1000
        z1 + z2 + c1.card.title.compareTo(c2.card.title)
    }

    private val callback = object : DeckLineCallback {
        override fun multChanged(deckLine: DeckLine, mult: Int) {
            log.d("multChanged: [$mult] $deckLine")
        }
    }

    override fun onResume() {
        super.onResume()

        val deckId = intent.getStringExtra(DECK_EXTRA)
        val cards = customApplication.cardListBuilder.build(deckId.toInt()).all()

        find<RecyclerView>(R.id.deck_recyclerview).apply {
            layoutManager = LinearLayoutManager(this@DeckDetailActivity)
            adapter = DeckDetailAdapter(
                    cards.sortedWith(cardComparator),
                    this@DeckDetailActivity).apply {
                deckLineCallback = callback
            }
        }

        val deckStats = DeckStats(cards)

        val textView = findViewById(R.id.deck_stats) as TextView
        textView.text = """
            colors: ${deckStats.colors.joinToString()}
            formats: ${deckStats.format}
            cards: ${deckStats.mainDeck.map { it.mult }.sum()}
            sidebard: ${deckStats.sideboard.map { it.mult }.sum()}
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
        R.id.deckdetails_delete -> {
            // FIXME add confirmation
            customApplication.deckList.delete(deck)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
