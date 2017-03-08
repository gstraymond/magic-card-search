package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckDetailFragmentPagerAdapter
import fr.gstraymond.biz.DeckManager
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.models.Deck
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
    private val deckManager by lazy { DeckManager(app().deckList, app().cardListBuilder) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deckId = intent.getStringExtra(DECK_EXTRA)
        deck = app().deckList.getByUid(deckId)!!

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = deck.name
        }

        val viewPager = find<ViewPager>(R.id.viewpager)
        viewPager.adapter = DeckDetailFragmentPagerAdapter(supportFragmentManager)

        find<TabLayout>(R.id.sliding_tabs).setupWithViewPager(viewPager)
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
