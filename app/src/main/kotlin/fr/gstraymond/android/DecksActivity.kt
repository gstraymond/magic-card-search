package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.github.clans.fab.FloatingActionMenu
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckListAdapter
import fr.gstraymond.models.Deck
import fr.gstraymond.utils.*

class DecksActivity : CustomActivity(R.layout.activity_decks) {

    private val sortSpinner by lazy { find<Spinner>(R.id.sort_chooser) }
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

    private val floatingMenu by lazy { find<FloatingActionMenu>(R.id.decks_floating_menu) }

    companion object {
        fun getIntent(context: Context) = Intent(context, DecksActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.decks_title)

        find<View>(R.id.decks_fab_add).let {
            it.setOnClickListener {
                startActivity {
                    val deckId = app().deckManager.createEmptyDeck()
                    DeckDetailActivity.getIntent(this, "$deckId")
                }
            }
        }

        find<View>(R.id.decks_fab_import).let {
            it.setOnClickListener {
                startActivity {
                    DeckImporterActivity.getIntent(this)
                }
            }
        }

        find<View>(R.id.decks_fab_paste).let {
            it.setOnClickListener {
                startActivity {
                    DeckPasteActivity.getIntent(this)
                }
            }
        }

        find<RecyclerView>(R.id.decks_recyclerview).let {
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = deckListAdapter
        }

        sortSpinner.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                SortTypes.values().map {
                    resources.getString(resources.getIdentifier("decks_sort_spinner_${it.name.toLowerCase()}", "string", packageName))
                }
        )

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                deckListAdapter.sort = SortTypes.values()[p2]
                prefs.decksSort = deckListAdapter.sort
                updateDecks()
            }
        }
    }

    enum class SortTypes { Format, Alpha }

    override fun onResume() {
        super.onResume()
        floatingMenu.close(false)
        deckListAdapter.sort = prefs.decksSort
        sortSpinner.setSelection(SortTypes.values().indexOf(prefs.decksSort))
        updateDecks()
        if (app().deckList.isEmpty()) {
            visible(R.id.decks_empty_text)
        } else {
            gone(R.id.decks_empty_text)
        }
    }

    private fun updateDecks() {
        deckListAdapter.apply {
            decks = getSortedDecks()
            notifyDataSetChanged()
        }
    }

    private fun getSortedDecks() =
            app().deckList.all().sortedBy(Deck::timestamp).reversed()
}