package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckListAdapter
import fr.gstraymond.models.Deck
import fr.gstraymond.utils.find
import fr.gstraymond.utils.hide
import fr.gstraymond.utils.show

class DeckListActivity : CustomActivity() {

    companion object {
        fun getIntent(context: Context) = Intent(context, DeckListActivity::class.java)
    }

    private lateinit var deckListAdapter: DeckListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_list)

        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.decklist_title)
        }

        deckListAdapter = DeckListAdapter(this).apply {
            onClickListener = { deckId ->
                View.OnClickListener {
                    startActivity(DeckDetailActivity.getIntent(this@DeckListActivity, deckId))
                }
            }
        }

        find<RecyclerView>(R.id.deck_recyclerview).let { it ->
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = deckListAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        updateDecks()
        if (getDecks().isEmpty()) {
            show(R.id.deck_list_empty_text)
        } else {
            hide(R.id.deck_list_empty_text)
        }
    }

    private fun updateDecks() {
        deckListAdapter.apply {
            decks = getSortedDecks()
            notifyDataSetChanged()
        }
    }

    private fun getDecks() = customApplication.decklist.elems

    private fun getSortedDecks() = getDecks().sortedBy(Deck::timestamp).reversed()

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
