package fr.gstraymond.biz

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.view.View
import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.R
import fr.gstraymond.android.DataUpdater
import fr.gstraymond.models.search.response.Hit
import fr.gstraymond.models.search.response.SearchResult

// FIXME Useful ?
class UIUpdater(private val dataUpdater: DataUpdater,
                objectMapper: Moshi,
                private val context: Context,
                private val rootView: View) {
    private val mapperUtil = MapperUtil.fromType(objectMapper, SearchResult::class.java)

    fun update(result: String) {
        val searchResult = mapperUtil.read(result)
        UiUpdater.update(searchResult, dataUpdater, context, rootView)
    }
}

object UiUpdater {

    fun update(result: SearchResult?,
               dataUpdater: DataUpdater,
               context: Context,
               rootView: View) {
        if (result == null) {
            showText(context.getString(R.string.failed_search), dataUpdater, rootView)
            return
        }

        val cards = result.hits.hits.map(Hit::_source)
        val totalCardCount = result.hits.total

        val textId =
                if (totalCardCount <= 1) R.string.progress_card_found
                else R.string.progress_cards_found

        dataUpdater.updateCards(totalCardCount, cards)
        dataUpdater.updateFacets(result)

        val text = String.format("%s/%s %s", dataUpdater.adapterItemCount(), totalCardCount, context.getString(textId))
        showText(text, dataUpdater, rootView)
    }

    private fun showText(message: String,
                         dataUpdater: DataUpdater,
                         rootView: View) {
        dataUpdater.getLoadingSnackbar()?.dismiss()
        val snackbar = Snackbar.make(rootView, message, LENGTH_LONG)
        dataUpdater.setLoadingSnackbar(snackbar)
        snackbar.show()
    }
}