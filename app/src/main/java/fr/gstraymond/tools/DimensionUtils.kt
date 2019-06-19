package fr.gstraymond.tools

import android.content.Context
import android.util.DisplayMetrics

fun dpToPx(context: Context, dp: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}