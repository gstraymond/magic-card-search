package fr.gstraymond.ui

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import android.widget.Toast

import com.magic.card.search.commons.log.Log

import fr.gstraymond.R
import fr.gstraymond.android.CardListActivity
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SearchProcessor

import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText

class EndScrollListener(private val activity: CardListActivity,
                        private val layoutManager: LinearLayoutManager,
                        private val fab: FloatingActionButton) : RecyclerView.OnScrollListener() {

    var canLoadMoreItems = true

    private val log = Log(this)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (canLoadMoreItems && hasEndReached(layoutManager)) {
            if (layoutManager.itemCount != activity.totalCardCount) {
                log.i("onScroll - endReached")
                val options = activity.currentSearch.updateAppend(true).updateAddToHistory(false)
                showLoadingToast()
                SearchProcessor(activity, options, R.string.loading_more).execute()
            } else {
                fab.hide()
            }
        } else {
            fab.show()
        }
    }

    private fun hasEndReached(layoutManager: LinearLayoutManager) =
            (layoutManager.findFirstVisibleItemPosition() + layoutManager.childCount) >= layoutManager.itemCount

    private fun showLoadingToast() {
        val loadingToast = makeText(activity, R.string.loading_more, LENGTH_SHORT)
        activity.loadingToast = loadingToast
        loadingToast.show()
    }
}
