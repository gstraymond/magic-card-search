package fr.gstraymond.ui.adapter.card.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class View<in A>(context: Context,
                          private val layoutId: Int) {

    private val inflater = LayoutInflater.from(context)

    protected abstract fun getView(item: A, view: View): View

    fun getView(item: A, convertView: View?, parent: ViewGroup): View =
            getView(item, convertView ?: inflater.inflate(layoutId, null))
}