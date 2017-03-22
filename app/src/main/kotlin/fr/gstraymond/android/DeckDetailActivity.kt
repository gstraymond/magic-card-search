package fr.gstraymond.android

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.BufferType.EDITABLE
import fr.gstraymond.R
import fr.gstraymond.analytics.Tracker
import fr.gstraymond.android.adapter.DeckDetailFragmentPagerAdapter
import fr.gstraymond.biz.DeckManager
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.models.Deck
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.inflate
import fr.gstraymond.utils.startActivity

class DeckDetailActivity : CustomActivity(R.layout.activity_deck_detail) {

    companion object {
        val DECK_EXTRA = "deck"

        fun getIntent(context: Context, deckId: String) =
                Intent(context, DeckDetailActivity::class.java).apply {
                    putExtra(DECK_EXTRA, deckId)
                }
    }

    private lateinit var deck: Deck
    private lateinit var deckTitle: TextView
    private val deckManager by lazy { DeckManager(app().deckList, app().cardListBuilder) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deckId = intent.getStringExtra(DECK_EXTRA)
        deck = app().deckList.getByUid(deckId)!!

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        deckTitle = find<TextView>(R.id.toolbar_text).apply {
            text = deck.name
            setOnClickListener {
                createDialog(context)
            }
        }

        val viewPager = find<ViewPager>(R.id.viewpager)
        viewPager.adapter = DeckDetailFragmentPagerAdapter(supportFragmentManager)

        find<TabLayout>(R.id.sliding_tabs).setupWithViewPager(viewPager)
    }

    private fun createDialog(context: Context) {
        val view = context.inflate(R.layout.activity_deck_detail_title)
        val editText = view.find<EditText>(R.id.deck_detail_title)
        editText.setText(deck.name, EDITABLE)
        AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    updateDeckName(editText.text.toString())
                })
                .setNegativeButton(android.R.string.cancel, { _, _ -> })
                .create()
                .show()
    }

    private fun updateDeckName(deckName: String) {
        deck = deck.copy(name = deckName)
        app().deckList.update(deck)
        deckTitle.text = deckName
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.deck_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.deckdetails_add -> startActivity {
            CardListActivity.getIntent(this, SearchOptions(deckId = intent.getStringExtra(DECK_EXTRA)))
        }.run {
            true
        }

        R.id.deckdetails_delete -> {
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.deckdetails_delete_title))
                    .setPositiveButton(getString(R.string.deckdetails_delete_ok)) { _, _ ->
                        deckManager.delete(deck)
                        Tracker.addRemoveDeck(added = false)
                        finish()
                    }
                    .setNegativeButton(getString(R.string.deckdetails_delete_cancel)) { _, _ -> }
                    .show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
