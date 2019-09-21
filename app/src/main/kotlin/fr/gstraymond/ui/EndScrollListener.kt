package fr.gstraymond.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.magic.card.search.commons.log.Log
import fr.gstraymond.android.DataUpdater
import fr.gstraymond.biz.SearchProcessorBuilder

class EndScrollListener(private val searchProcessorBuilder: SearchProcessorBuilder,
                        private val dataUpdater: DataUpdater,
                        private val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    private val log = Log(this)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dataUpdater.isSearchAvailable()
                && hasEndReached(layoutManager)
                && layoutManager.itemCount != dataUpdater.getTotalItemCount()) {
            log.i("onScroll - endReached")
            val options = dataUpdater.getCurrentSearch().updateAppend(true).updateAddToHistory(false)
            searchProcessorBuilder.build().execute(options)
        }
    }

    private fun hasEndReached(layoutManager: LinearLayoutManager) =
            (layoutManager.findFirstVisibleItemPosition() + layoutManager.childCount) >=
                    layoutManager.itemCount
}
