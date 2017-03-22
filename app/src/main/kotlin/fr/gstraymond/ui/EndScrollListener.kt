package fr.gstraymond.ui

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.magic.card.search.commons.log.Log
import fr.gstraymond.android.CardListActivity
import fr.gstraymond.biz.SearchProcessor

class EndScrollListener(private val activity: CardListActivity,
                        private val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    var canLoadMoreItems = true
    var fab: FloatingActionButton? = null

    private val log = Log(this)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (canLoadMoreItems && hasEndReached(layoutManager)) {
            if (layoutManager.itemCount != activity.totalCardCount) {
                log.i("onScroll - endReached")
                val options = activity.currentSearch.updateAppend(true).updateAddToHistory(false)
                SearchProcessor(activity, options).execute()
            } else {
                fab?.hide()
            }
        } else {
            fab?.show()
        }
    }

    private fun hasEndReached(layoutManager: LinearLayoutManager) =
            (layoutManager.findFirstVisibleItemPosition() + layoutManager.childCount) >= layoutManager.itemCount
}
