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
import fr.gstraymond.android.adapter.DeckDetailAdapter
import fr.gstraymond.android.adapter.DeckLineCallback
import fr.gstraymond.biz.DeckManager
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.constants.Consts
import fr.gstraymond.db.json.CardList
import fr.gstraymond.models.Deck
import fr.gstraymond.models.DeckLine
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find

class DeckDetailActivity : CustomActivity(R.layout.activity_deck_detail) {

    companion object {
        val DECK_EXTRA = "deck"

        fun getIntent(context: Context, deckId: String) =
                Intent(context, DeckDetailActivity::class.java).apply {
                    putExtra(DECK_EXTRA, deckId)
                }
    }

    private lateinit var deck: Deck
    private lateinit var cardList: CardList
    private val deckManager by lazy { DeckManager(app().deckList, app().cardListBuilder) }

    private val log = Log(javaClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deckId = intent.getStringExtra(DECK_EXTRA)
        deck = app().deckList.getByUid(deckId)!!
        cardList = app().cardListBuilder.build(deckId.toInt())

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = deck.name
        }
    }

    private val callback = object : DeckLineCallback {
        override fun multChanged(deckLine: DeckLine, mult: Int) {
            log.d("multChanged: [$mult] $deckLine")
            when (mult) {
                0 -> cardList.delete(deckLine)
                else -> cardList.update(deckLine.copy(mult = mult))
            }
            updateStats()
        }

        override fun sideboardChanged(deckLine: DeckLine, sideboard: Boolean) {
            log.d("sideboardChanged: [$sideboard] $deckLine")
            cardList.update(deckLine.copy(isSideboard = sideboard), updateDeck = false)
            updateStats()
        }

        override fun cardClick(deckLine: DeckLine) {
            startActivity {
                Intent(this@DeckDetailActivity, CardDetailActivity::class.java).apply {
                    putExtra(Consts.CARD, deckLine.card)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        find<RecyclerView>(R.id.deck_recyclerview).apply {
            layoutManager = LinearLayoutManager(this@DeckDetailActivity)
            adapter = DeckDetailAdapter(cardList, this@DeckDetailActivity).apply {
                deckLineCallback = callback
            }
        }

        updateStats()
    }

    private fun updateStats() {
        val deckStats = DeckStats(cardList.all())
        find<TextView>(R.id.deck_colors).text = "colors: ${deckStats.colors.joinToString()}"
        find<TextView>(R.id.deck_formats).text = "formats: ${deckStats.format}"
        find<TextView>(R.id.deck_cards).text = "cards: ${deckStats.mainDeck.map { it.mult }.sum()}"
        find<TextView>(R.id.deck_sideboard).text = "sidebard: ${deckStats.sideboard.map { it.mult }.sum()}"
        find<TextView>(R.id.deck_price).text = "price: ${deckStats.totalPrice}"
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
            deckManager.delete(deck)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
