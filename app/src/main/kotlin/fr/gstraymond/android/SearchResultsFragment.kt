package fr.gstraymond.android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.CardListActivity.Companion.SEARCH_QUERY
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SearchProcessorBuilder
import fr.gstraymond.biz.UIUpdater
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.EndScrollListener
import fr.gstraymond.ui.adapter.CardArrayAdapter
import fr.gstraymond.ui.adapter.CardArrayData
import fr.gstraymond.utils.app
import fr.gstraymond.utils.startActivity

class SearchResultsFragment : Fragment(), CardArrayAdapter.ClickCallbacks {

    private val dataUpdater by lazy { activity as CardListActivity }
    private val rootView by lazy { activity.findViewById(android.R.id.content) }
    private val searchProcessorBuilder by lazy { SearchProcessorBuilder(dataUpdater, activity.app().elasticSearchClient, activity, rootView) }
    private val uiUpdater by lazy { UIUpdater(dataUpdater, activity.app().objectMapper, activity, rootView) }

    private lateinit var arrayAdapter: CardArrayAdapter
    private lateinit var recyclerView: RecyclerView

    private val log = Log(javaClass)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_search_results, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view as RecyclerView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val data = dataUpdater.getCurrentSearch().deckId?.run {
            val deck = activity.app().deckList.getByUid(this)
            CardArrayData(
                    cards = null,
                    deck = deck!! to app().cardListBuilder.build(toInt()))
        } ?: CardArrayData(
                cards = app().wishList,
                deck = null)

        arrayAdapter = CardArrayAdapter(rootView, data, this, dataUpdater)

        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = arrayAdapter
            addOnScrollListener(EndScrollListener(searchProcessorBuilder, dataUpdater, linearLayoutManager))
        }

        val savedSearch = savedInstanceState?.getParcelable<SearchOptions>(SEARCH_QUERY)
                ?: activity.intent.getParcelableExtra<SearchOptions>(SEARCH_QUERY)

        savedSearch?.apply {
            dataUpdater.setCurrentSearch(this)
        }

        val resultAsString = activity.intent.getStringExtra(CardListActivity.CARD_RESULT)
        if (savedSearch == null && resultAsString != null) {
            log.d("onCreateView: resultAsString $resultAsString")
            uiUpdater.update(resultAsString)
        } else {
            log.d("onCreateView: getCurrentSearch ${dataUpdater.getCurrentSearch()}")
            searchProcessorBuilder.build().execute(dataUpdater.getCurrentSearch())
        }
    }

    override fun cardClicked(card: Card) = startActivity {
        CardDetailActivity.getIntent(activity, card)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (dataUpdater.getCurrentSearch() != SearchOptions()) {
            outState.putParcelable(SEARCH_QUERY, dataUpdater.getCurrentSearch())
        }
        super.onSaveInstanceState(outState)
    }

    fun itemCount() = arrayAdapter.itemCount

    fun appendCards(cards: List<Card>) = arrayAdapter.appendCards(cards)

    fun setCards(cards: List<Card>) = arrayAdapter.setCards(cards)
}