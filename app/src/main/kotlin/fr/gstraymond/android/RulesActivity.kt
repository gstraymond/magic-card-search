package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.adapter.RulesAdapter
import fr.gstraymond.android.adapter.RulesCallback
import fr.gstraymond.db.json.LazyJsonList
import fr.gstraymond.utils.*
import kotlin.math.min

class RulesActivity : CustomActivity(R.layout.activity_rules), RulesCallback, LazyJsonList.LoadingCallback {

    companion object {
        const val HISTORY = "history"

        fun getIntent(context: Context) =
                Intent(context, RulesActivity::class.java)
    }

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RulesAdapter

    private val topTextView by lazy { find<TextView>(R.id.toolbar_top) }
    private val backTextView by lazy { find<TextView>(R.id.toolbar_back) }
    private val progressBar by lazy { find<ProgressBar>(R.id.rules_progressbar) }
    private val emptyText by lazy { find<TextView>(R.id.rules_empty_text) }
    private val searchView by lazy { find<SearchView>(R.id.search_input) }
    private val scrollStatus by lazy { find<TextView>(R.id.scroll_status) }
    private val searchStatus by lazy { find<TextView>(R.id.search_status) }
    private val searchPrevious by lazy { find<ImageButton>(R.id.search_previous) }
    private val searchNext by lazy { find<ImageButton>(R.id.search_next) }

    private val ruleList by lazy { app().ruleList }

    private val history = mutableListOf<Int>()
    private var searchResults = listOf<Int>()
    private var searchPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ruleList.registerLoading(this)

        savedInstanceState?.apply {
            history.addAll(getIntegerArrayList(HISTORY)!!)
            if (history.isNotEmpty()) backTextView.visible()
        }

        setSupportActionBar(find(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.rules_title)

        adapter = RulesAdapter(this).apply { rulesCallback = this@RulesActivity }

        find<RecyclerView>(R.id.rules_recyclerview).let {
            linearLayoutManager = LinearLayoutManager(this)
            it.layoutManager = linearLayoutManager
            it.adapter = adapter
            it.setOnTouchListener { _, _ ->
                resetSearchFocus()
                false
            }
            it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    updateStatus(linearLayoutManager.findFirstVisibleItemPosition())
                }
            })
        }

        topTextView.setOnClickListener {
            scroll(0)
            resetSearchFocus()
        }

        backTextView.setOnClickListener {
            val position = history.removeAt(history.size - 1)
            scroll(position)
            if (history.isEmpty()) backTextView.gone()
            resetSearchFocus()
        }

        searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(text: String): Boolean {
                        synchronized(this@RulesActivity) {
                            val tokens = text.lowercase().split(" ").filter { it.isNotBlank() }
                            searchResults = tokens.foldIndexed(setOf<Int>()) { index, acc, word ->
                                when (index) {
                                    0 -> ruleList.trie.get(word)
                                    else -> acc.intersect(ruleList.trie.get(word))
                                }
                            }.sorted()
                            searchPosition = 0
                            updateResult()
                            adapter.highlightWords = listOf()
                            searchResults.firstOrNull()?.apply {
                                scroll(this)
                                adapter.highlightWords = tokens
                            }
                            adapter.notifyDataSetChanged()
                        }

                        return true
                    }

                    override fun onQueryTextSubmit(text: String) = true
                })

        searchPrevious.setOnClickListener {
            if (searchResults.isNotEmpty()) {
                searchPosition = when (searchPosition) {
                    0 -> searchResults.size - 1
                    else -> searchPosition - 1
                }
                updateResult()
                scroll(searchResults[searchPosition])
                resetSearchFocus()
            }
        }

        searchNext.setOnClickListener {
            if (searchResults.isNotEmpty()) {
                searchPosition = when (searchPosition) {
                    searchResults.size - 1 -> 0
                    else -> searchPosition + 1
                }
                updateResult()
                scroll(searchResults[searchPosition])
                resetSearchFocus()
            }
        }

        updateResult()

        if (ruleList.isLoaded()) loaded()
    }

    private fun resetSearchFocus() {
        if (searchView.hasFocus()) searchView.clearFocus()
    }

    override fun onResume() {
        super.onResume()
        findView(R.id.root_view).requestFocus()
    }

    override fun loaded() {
        ruleList.unregisterLoading(this)
        adapter.rules = ruleList.all()

        runOnUiThread {
            progressBar.gone()
            adapter.notifyDataSetChanged()
            if (ruleList.isEmpty()) emptyText.visible()
            else updateStatus(0)
        }
    }

    override fun scrollTo(position: Int) {
        backTextView.visible()
        history.add(linearLayoutManager.findFirstVisibleItemPosition())
        scroll(position)
    }

    private fun scroll(position: Int) {
        linearLayoutManager.scrollToPositionWithOffset(position, 0)
        updateStatus(position)
    }

    private fun updateStatus(position: Int) {
        val percent = 100 * position / adapter.itemCount
        scrollStatus.text = "%s/%s — %s%%".format(position + 1, adapter.itemCount, percent)
    }

    private fun updateResult() {
        searchStatus.text = "%s/%s".format(min(searchResults.size, searchPosition + 1), searchResults.size)
    }

    override fun browse(url: String) = startActivity {
        Intent(Intent.ACTION_VIEW).apply { data = Uri.parse("http://$url") }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntegerArrayList(HISTORY, ArrayList(history))
        super.onSaveInstanceState(outState)
    }
}