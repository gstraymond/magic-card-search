package fr.gstraymond.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import fr.gstraymond.android.CustomApplication

inline fun <reified A : View> View.find(id: Int): A = findViewById(id) as A

inline fun <reified A : View> Activity.find(id: Int): A = findViewById(id) as A

fun View.hide(id: Int): Unit {
    findViewById(id).visibility = GONE
}

fun View.show(id: Int): Unit {
    findViewById(id).visibility = VISIBLE
}

fun Activity.hide(id: Int): Unit {
    findViewById(id).visibility = GONE
}

fun Activity.show(id: Int): Unit {
    findViewById(id).visibility = VISIBLE
}

fun Activity.startActivity(buildIntent: () -> Intent) {
    startActivity(buildIntent())
}

fun Activity.app(): CustomApplication = application as CustomApplication

fun Resources.drawable(id: Int): Drawable = ResourcesCompat.getDrawable(this, id, null)!!

fun Resources.colorStateList(id: Int): ColorStateList = ResourcesCompat.getColorStateList(this, id, null)!!

fun Resources.color(id: Int): Int = ResourcesCompat.getColor(this, id, null)

fun Fragment.app(): CustomApplication = activity.app()

fun Fragment.startActivity(buildIntent: () -> Intent) {
    activity.startActivity(buildIntent)
}

fun Context.inflate(layoutId: Int): View = LayoutInflater.from(this).inflate(layoutId, null)

fun Context.inflate(layoutId: Int, parent: ViewGroup): View = LayoutInflater.from(this).inflate(layoutId, parent, false)

fun Context.startActivity(buildIntent: () -> Intent) {
    startActivity(buildIntent())
}