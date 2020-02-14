package fr.gstraymond.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.constants.FacetConst
import fr.gstraymond.models.search.response.Aggregations
import fr.gstraymond.models.search.response.Bucket
import fr.gstraymond.utils.find
import fr.gstraymond.utils.gone
import fr.gstraymond.utils.inflate
import fr.gstraymond.utils.visible


class FacetListAdapter(aggregationsMap: Map<String, Aggregations>?,
                       private val options: SearchOptions, context: Context) : BaseExpandableListAdapter() {
    private val mutableFacetMap = aggregationsMap?.toMutableMap() ?: mutableMapOf()
    private val facetList = mutableListOf<String>()
    private val selectedFacets = mutableListOf<String>()
    private val selectedTerms = mutableListOf<Bucket>()

    init {
        for (facetAsString in FacetConst.getFacetOrder()) {
            val facet = mutableFacetMap[facetAsString]

            if (facet == null || facet.buckets.isEmpty()) {
                mutableFacetMap.remove(facetAsString)
                continue
            }

            facetList.add(facetAsString)
            val facetTerms = facet.buckets

            if (!facetTerms.isEmpty()) {
                val termsAsString = options.facets[facetAsString]
                if (termsAsString != null) {
                    selectedFacets.add(facetAsString)
                    selectedTerms.addAll(findTerms(termsAsString, facetTerms))
                }
            }
        }

        for ((facetAsString, facet) in mutableFacetMap) {
            if (showLoadMore(facet, facetAsString)) {
                val loadMoreTerm = Bucket(context.getString(R.string.facet_more), -1)
                facet.buckets.add(loadMoreTerm)
            }
        }
    }

    private fun showLoadMore(aggregations: Aggregations, facetAsString: String): Boolean {
        val facetSizeRequested = options.facetSize[facetAsString] ?: 10
        return aggregations.buckets.size == facetSizeRequested
    }

    private fun findTerms(termsAsString: List<String>, buckets: List<Bucket>) = termsAsString.mapNotNull { findTerm(it, buckets) }

    private fun findTerm(termAsString: String, buckets: List<Bucket>) = buckets.firstOrNull { it.key == termAsString }

    private fun getChildren(groupPosition: Int): List<Bucket> = mutableFacetMap[getGroup(groupPosition)]!!.buckets

    override fun getChild(groupPosition: Int, childPosition: Int) = getTerm(groupPosition, childPosition)

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun getChildView(groupPosition: Int,
                              childPosition: Int,
                              isLastChild: Boolean,
                              convertView: View?,
                              parent: ViewGroup): View {
        val view = convertView ?: parent.context.inflate(R.layout.drawer_child)

        val term = getTerm(groupPosition, childPosition)

        view.find<TextView>(R.id.drawer_child_text).text = term.key

        val counterTextViewInactive = view.find<TextView>(R.id.drawer_child_counter_inactive)
        val counterTextViewActive = view.find<TextView>(R.id.drawer_child_counter_active)

        val (counterTextView, hiddenCounterTextView) = if (selectedTerms.contains(term)) {
            counterTextViewActive to counterTextViewInactive
        } else {
            counterTextViewInactive to counterTextViewActive
        }

        hiddenCounterTextView.gone()

        counterTextView.visible()
        counterTextView.text =
                if (term.doc_count > 0) term.doc_count.toString() + ""
                else "?"

        return view
    }

    fun getTerm(groupPosition: Int, childPosition: Int): Bucket = getChildren(groupPosition)[childPosition]

    override fun getChildrenCount(groupPosition: Int): Int = getChildren(groupPosition).size

    override fun getGroup(groupPosition: Int) = getFacet(groupPosition)

    fun getFacet(groupPosition: Int): String = facetList[groupPosition]

    override fun getGroupCount() = facetList.size

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: parent.context.inflate(R.layout.drawer_group)

        val facet = facetList[groupPosition]
        val groupTextView = view.find<TextView>(R.id.drawer_group_textview)
        groupTextView.text = FacetConst.getFacetName(facet, parent.context)

        val selectedTextView = view.find<TextView>(R.id.drawer_group_selected_textview)
        if (selectedFacets.contains(facet)) {
            val children = getChildren(groupPosition).filter { selectedTerms.contains(it) }
            selectedTextView.text = children.map(Bucket::key).joinToString()
            selectedTextView.visible()
        } else {
            selectedTextView.gone()
        }

        return view
    }

    override fun hasStableIds() = false

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    fun isTermSelected(bucket: Bucket) = selectedTerms.contains(bucket)
}
