package fr.gstraymond.ui

import android.view.View
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnChildClickListener
import fr.gstraymond.android.CardListActivity
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SearchProcessor
import fr.gstraymond.ui.adapter.FacetListAdapter

class FacetOnChildClickListener(private val adapter: FacetListAdapter,
                                private val options: SearchOptions,
                                private val activity: CardListActivity) : OnChildClickListener {

    override fun onChildClick(parent: ExpandableListView,
                              view: View,
                              groupPosition: Int,
                              childPosition: Int,
                              id: Long): Boolean {
        val term = adapter.getTerm(groupPosition, childPosition)
        val facet = adapter.getFacet(groupPosition)

        options.updateAppend(false).updateFrom(0).updateAddToHistory(true)

        if (term.count > -1) {
            if (adapter.isTermSelected(term)) {
                options.removeFacet(facet, term.term)
            } else {
                options.addFacet(facet, term.term)
            }
        } else {
            options.addFacetSize(facet)
        }

        if ("*" == options.query && options.facets.isEmpty()) {
            options.updateAddToHistory(false)
        }

        SearchProcessor(activity, options).execute()
        return true
    }

}
