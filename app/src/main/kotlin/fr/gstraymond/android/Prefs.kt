package fr.gstraymond.android

import android.content.Context
import fr.gstraymond.android.DecksActivity.*

class Prefs(context: Context) {
    private val filename = "mtg.search.prefs"

    private val galleryModeParam = "gallery_mode"
    private val frenchEnabledParam = "french_enabled"
    private val rulesVersionParam = "rules_version"
    private val deckCardSortParam = "deck_card_sort"
    private val decksSortParam = "decks_sort"

    private val prefs = context.getSharedPreferences(filename, 0)

    var galleryMode: Boolean
        get() = prefs.getBoolean(galleryModeParam, false)
        set(value) = prefs.edit().putBoolean(galleryModeParam, value).apply()

    var frenchEnabled: Boolean
        get() = prefs.getBoolean(frenchEnabledParam, true)
        set(value) = prefs.edit().putBoolean(frenchEnabledParam, value).apply()

    var rulesVersion: String
        get() = prefs.getString(rulesVersionParam, "")
        set(value) = prefs.edit().putString(rulesVersionParam, value).apply()

    var deckCardSort: Boolean
        get() = prefs.getBoolean(deckCardSortParam, false)
        set(value) = prefs.edit().putBoolean(deckCardSortParam, value).apply()

    var decksSort: SortTypes
        get() = SortTypes.valueOf(prefs.getString(decksSortParam, SortTypes.Format.name))
        set(value) = prefs.edit().putString(decksSortParam, value.name).apply()
}