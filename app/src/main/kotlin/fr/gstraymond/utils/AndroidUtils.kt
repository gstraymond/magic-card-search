package fr.gstraymond.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import fr.gstraymond.android.CustomApplication

inline fun <reified A : View> View.find(id: Int): A = findViewById(id)

inline fun <reified A : View> Activity.find(id: Int): A = findViewById(id)

fun View.findView(id: Int): View = findViewById(id)

fun Activity.findView(id: Int): View = findViewById(id)

fun View.gone() {
    visibility = GONE
}

fun View.gone(id: Int) {
    findView(id).gone()
}

fun View.visible() {
    visibility = VISIBLE
}

fun View.visible(id: Int) {
    findView(id).visible()
}

fun View.invisible() {
    visibility = INVISIBLE
}

fun Activity.gone(id: Int) {
    findView(id).visibility = GONE
}

fun Activity.visible(id: Int) {
    findView(id).visibility = VISIBLE
}

fun Activity.startActivity(buildIntent: () -> Intent) {
    startActivity(buildIntent())
}

fun Activity.app(): CustomApplication = application as CustomApplication

fun Activity.hasPerms(vararg permissions: String) = permissions.toList().all {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

fun Activity.requestPerms(code: Int, vararg permissions: String) {
    ActivityCompat.requestPermissions(this, permissions, code)
}

fun Resources.drawable(id: Int): Drawable = ResourcesCompat.getDrawable(this, id, null)!!

fun Resources.colorStateList(id: Int): ColorStateList = ResourcesCompat.getColorStateList(this, id, null)!!

fun Resources.color(id: Int): Int = ResourcesCompat.getColor(this, id, null)

fun Fragment.app(): CustomApplication = activity!!.app()

fun Fragment.startActivity(buildIntent: () -> Intent) {
    activity!!.startActivity(buildIntent)
}

fun Context.inflate(layoutId: Int): View = LayoutInflater.from(this).inflate(layoutId, null)

fun Context.inflate(layoutId: Int, parent: ViewGroup): View = LayoutInflater.from(this).inflate(layoutId, parent, false)

fun Context.startActivity(buildIntent: () -> Intent) {
    startActivity(buildIntent())
}