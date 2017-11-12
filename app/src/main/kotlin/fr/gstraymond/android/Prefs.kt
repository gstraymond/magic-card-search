package fr.gstraymond.android

import android.content.Context

class Prefs(context: Context) {
    private val filename = "mtg.search.prefs"
    private val galleryModeParam = "gallery_mode"
    private val prefs = context.getSharedPreferences(filename, 0)

    var galleryMode: Boolean
        get() = prefs.getBoolean(galleryModeParam, false)
        set(value) = prefs.edit().putBoolean(galleryModeParam, value).apply()
}