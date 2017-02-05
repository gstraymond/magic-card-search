package fr.gstraymond.android

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import fr.gstraymond.R
import fr.gstraymond.android.fragment.HistoryListFragment
import fr.gstraymond.constants.Consts.HISTORY_LIST
import fr.gstraymond.utils.find

class HistoryActivity : CustomActivity(R.layout.activity_history) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        actionBarSetDisplayHomeAsUpEnabled(true)

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
                    .setPositiveButton(getString(R.string.history_alert_ok)) { dialog, whichButton ->
                        jsonHistoryDataSource.clearNonFavoriteHistory()
                        showHistory()
                    }
                    .setNegativeButton(getString(R.string.history_alert_cancel)) { dialog, whichButton -> }
                    .show()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu) =
            menuInflater.inflate(R.menu.card_history_menu, menu).run { true }

    private fun showHistory() {
        val allHistory = jsonHistoryDataSource.allHistory

        val bundle = Bundle()
        bundle.putParcelableArrayList(HISTORY_LIST, allHistory)

        replaceFragment(HistoryListFragment(), R.id.history_fragment, bundle)
    }
}
