package fr.gstraymond.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.constants.FacetConst
import fr.gstraymond.models.search.response.Facet
import fr.gstraymond.models.search.response.Term
import fr.gstraymond.utils.find
import fr.gstraymond.utils.hide
import fr.gstraymond.utils.inflate
import fr.gstraymond.utils.show


class FacetListAdapter(facetMap: Map<String, Facet>,
                       private val options: SearchOptions, context: Context) : BaseExpandableListAdapter() {

    private val mutableFacetMap = facetMap.toMutableMap()
    private val facetList = mutableListOf<String>()
    private val selectedFacets = mutableListOf<String>()
    private val selectedTerms = mutableListOf<Term>()

    init {
        for (facetAsString in FacetConst.getFacetOrder()) {
            val facet = facetMap[facetAsString]

            if (facet == null || facet.terms.isEmpty()) {
                mutableFacetMap.remove(facetAsString)
                continue
            }

            facetList.add(facetAsString)
            val facetTerms = facet.terms

            if (!facetTerms.isEmpty()) {
                val termsAsString = options.facets[facetAsString]
                if (termsAsString != null) {
                    selectedFacets.add(facetAsString)
                    selectedTerms.addAll(findTerms(termsAsString, facetTerms))
                }
            }
        }

        mutableFacetMap.forEach { facetAsString, facet ->
            if (showLoadMore(facet, facetAsString)) {
                val loadMoreTerm = Term(context.getString(R.string.facet_more), -1)
                facet.terms.add(loadMoreTerm)
            }
        }
    }

    private fun showLoadMore(facet: Facet, facetAsString: String): Boolean {
        val facetSizeRequested = options.facetSize[facetAsString] ?: 10
        return facet.terms.size == facetSizeRequested
    }

    private fun findTerms(termsAsString: List<String>, terms: List<Term>) = termsAsString.mapNotNull { findTerm(it, terms) }

    private fun findTerm(termAsString: String, terms: List<Term>) = terms.firstOrNull { it.term == termAsString }

    private fun getChildren(groupPosition: Int): List<Term> = mutableFacetMap[getGroup(groupPosition)]!!.terms

    override fun getChild(groupPosition: Int, childPosition: Int) = getTerm(groupPosition, childPosition)

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun getChildView(groupPosition: Int,
                              childPosition: Int,
                              isLastChild: Boolean,
                              convertView: View?,
                              parent: ViewGroup): View {
        val view = convertView ?: parent.context.inflate(R.layout.drawer_child)

        val term = getTerm(groupPosition, childPosition)

        view.find<TextView>(R.id.drawer_child_text).text = term.term

        val counterTextViewInactive = view.find<TextView>(R.id.drawer_child_counter_inactive)
        val counterTextViewActive = view.find<TextView>(R.id.drawer_child_counter_active)

        val (counterTextView, hiddenCounterTextView) = if (selectedTerms.contains(term)) {
            counterTextViewActive to counterTextViewInactive
        } else {
            counterTextViewInactive to counterTextViewActive
        }

        hiddenCounterTextView.hide()

        counterTextView.show()
        counterTextView.text =
                if (term.count > 0) term.count.toString() + ""
                else "?"

        return view
    }

    fun getTerm(groupPosition: Int, childPosition: Int): Term = getChildren(groupPosition)[childPosition]

    override fun getChildrenCount(groupPosition: Int): Int = getChildren(groupPosition).size

    override fun getGroup(groupPosition: Int) = getFacet(groupPosition)

    fun getFacet(groupPosition: Int): String = facetList[groupPosition]

    override fun getGroupCount() = facetList.size

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: parent.context.inflate(R.layout.drawer_group)

        val facet = facetList[groupPosition]

        if (selectedFacets.contains(facet) || options.facetSize.containsKey(facet)) {
            val expandableListView = parent as ExpandableListView
            expandableListView.expandGroup(groupPosition)
        }

        val textView = view.find<TextView>(R.id.drawer_group_textview)
        textView.text = FacetConst.getFacetName(facet, parent.context)
        return view
    }

    override fun hasStableIds() = false

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    fun isTermSelected(term: Term) = selectedTerms.contains(term)
}
