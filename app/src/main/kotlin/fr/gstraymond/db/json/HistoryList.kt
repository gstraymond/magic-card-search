package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.models.History
import java.util.*

class HistoryList(context: Context,
                  moshi: Moshi) : JsonList<History>(
        context,
        MapperUtil.fromType(moshi, History::class.java),
        listName = "history") {

    override fun History.uid() = "$date"

    fun appendHistory(options: SearchOptions) {
        log.d("appendHistory: $options")
        append(History(options.query, false, options.facets, Date()))
    }

    fun clearNonFavoriteHistory() {
        log.d("clearNonFavoriteHistory")
        save(elems.filter { it.isFavorite })
    }

    fun manageFavorite(history: History, add: Boolean) {
        log.d("manageFavorite: $history <-> $add <-> $elems")
        getByUid(history.uid())?.apply { isFavorite = add }
        save(elems)
    }
}
