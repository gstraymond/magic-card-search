package fr.gstraymond.android

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import fr.gstraymond.R
import fr.gstraymond.android.fragment.HistoryListFragment
import fr.gstraymond.constants.Consts.HISTORY_LIST
import fr.gstraymond.models.Board
import fr.gstraymond.models.Board.DECK
import fr.gstraymond.utils.find
import java.util.*

class HistoryActivity : CustomActivity(R.layout.activity_history) {

    companion object {
        val DECK_ID_EXTRA = "deck_id"
        val BOARD_EXTRA = "board"

        fun getIntent(context: Context,
                      deckId: String? = null,
                      board: Board = DECK) =
                Intent(context, HistoryActivity::class.java).apply {
                    putExtra(DECK_ID_EXTRA, deckId)
                    putExtra(BOARD_EXTRA, board.ordinal)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find(R.id.toolbar))
        true.actionBarSetDisplayHomeAsUpEnabled()

        showHistory()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }

        R.id.history_clear_tab -> {
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.history_alert_title))
                    .setMessage(getString(R.string.history_alert_description))
                    .setPositiveButton(getString(R.string.history_alert_ok)) { _, _ ->
                        jsonHistoryDataSource.clearNonFavoriteHistory()
                        showHistory()
                    }
                    .setNegativeButton(getString(R.string.history_alert_cancel)) { _, _ -> }
                    .show()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu) =
            menuInflater.inflate(R.menu.card_history_menu, menu).run { true }

    private fun showHistory() {
        val bundle = Bundle().apply {
            val allHistory = ArrayList(jsonHistoryDataSource.all().sortedByDescending { it.date })
            putParcelableArrayList(HISTORY_LIST, allHistory)
            putString(DECK_ID_EXTRA, intent.getStringExtra(DECK_ID_EXTRA))
            putInt(BOARD_EXTRA, intent.getIntExtra(BOARD_EXTRA, 0))
        }

        replaceFragment(HistoryListFragment(), R.id.history_fragment, bundle)
    }
}
