package fr.gstraymond.android

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckListAdapter

class DeckListActivity : CustomActivity() {

    private var deckListAdapter: DeckListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_list)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Deck Manager" // getString(R.string.wishlist_title)
        }

        deckListAdapter = DeckListAdapter(this).apply {
            onClickListener = { deckId ->
                View.OnClickListener {
                    startActivity(DeckDetailActivity.getIntent(this@DeckListActivity, deckId))
                }
            }
        }

        (findViewById(R.id.deck_recyclerview) as RecyclerView).apply {
            layoutManager = LinearLayoutManager(this@DeckListActivity)
            adapter = deckListAdapter
        }

        if (getDecks().isNotEmpty()) {
            findViewById(R.id.deck_list_empty_text).visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateDecks()
    }

    private fun updateDecks() {
        deckListAdapter?.apply {
            decks = getSortedDecks()
            notifyDataSetChanged()
        }
    }

    private fun getDecks() = customApplication.decklist.elems

    private fun getSortedDecks() = getDecks().sortedBy { it.timestamp }.reversed()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.deck_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.decklist_import -> {
                startActivity(DeckImporterActivity.getIntent(this))
                return true
            }
            R.id.decklist_delete -> {
                /*deckListAdapter?.apply {
                    (0..itemCount - 1).forEach {
                    }
                }*/
                updateDecks()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
