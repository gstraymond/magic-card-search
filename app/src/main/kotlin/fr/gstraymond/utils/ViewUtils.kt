package fr.gstraymond.utils

import android.app.Activity
import android.view.View

inline fun <reified A : View> View.find(id: Int): A = findViewById(id) as A

inline fun <reified A : View> Activity.find(id: Int): A = findViewById(id) as A

fun Activity.hide(id: Int): Unit {
    findViewById(id).visibility = View.GONE
}

fun Activity.show(id: Int): Unit {
    findViewById(id).visibility = View.VISIBLE
}