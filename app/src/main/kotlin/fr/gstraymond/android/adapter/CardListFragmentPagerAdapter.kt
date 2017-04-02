package fr.gstraymond.android.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import fr.gstraymond.android.DataUpdater
import fr.gstraymond.android.SearchFiltersFragment
import fr.gstraymond.android.SearchResultsFragment
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.SearchResult

class CardListFragmentPagerAdapter(private val fragmentManager: FragmentManager,
                                   private val dataUpdater: DataUpdater) : FragmentStatePagerAdapter(fragmentManager) {

    // FIXME translate
    private val pageTitles = listOf("Filters", "Cards")

    // FIXME todo data structure for fragment and title
    private val fragments = listOf(SearchFiltersFragment(), SearchResultsFragment())

    override fun getCount() = fragments.size

    override fun getItem(position: Int) = fragments[position]

    override fun getPageTitle(position: Int) = pageTitles[position]

    fun updateCards(cards: List<Card>) {
        if (dataUpdater.getCurrentSearch().append) {
            getSearchResultsFragment().appendCards(cards)
        } else {
            getSearchResultsFragment().setCards(cards)
        }
    }

    fun updateFacets(result: SearchResult) {
        if (!dataUpdater.getCurrentSearch().append) {
            getSearchFiltersFragment().setFilters(result)
        }
    }

    fun adapterItemCount() = getSearchResultsFragment().itemCount()

    fun setSearchViewData(options: List<Option>) {
        getSearchFiltersFragment().setSearchViewData(options)
    }

    private fun getSearchResultsFragment() =
            fragmentManager.fragments.find { it is SearchResultsFragment } as SearchResultsFragment

    private fun getSearchFiltersFragment() =
            fragmentManager.fragments.find { it is SearchFiltersFragment } as SearchFiltersFragment
}