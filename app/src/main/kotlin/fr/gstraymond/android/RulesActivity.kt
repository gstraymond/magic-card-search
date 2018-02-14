package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ProgressBar
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.adapter.RulesAdapter
import fr.gstraymond.android.adapter.RulesCallback
import fr.gstraymond.db.json.LazyJsonList
import fr.gstraymond.utils.*

class RulesActivity : CustomActivity(R.layout.activity_rules), RulesCallback, LazyJsonList.LoadingCallback {

    companion object {
        const val HISTORY = "history"

        fun getIntent(context: Context) =
                Intent(context, RulesActivity::class.java)
    }

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    private val topTextView by lazy { find<TextView>(R.id.toolbar_top) }
    private val backTextView by lazy { find<TextView>(R.id.toolbar_back) }
    private val progressBar by lazy { find<ProgressBar>(R.id.rules_progressbar) }
    private val emptyText by lazy { find<TextView>(R.id.rules_empty_text) }

    private val history = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app().ruleList.registerLoading(this)

        savedInstanceState?.apply {
            history.addAll(getIntegerArrayList(HISTORY))
            if (history.isNotEmpty()) backTextView.visible()
        }

        setSupportActionBar(find(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.rules_title)

        recyclerView = find<RecyclerView>(R.id.rules_recyclerview).apply {
            linearLayoutManager = LinearLayoutManager(this@RulesActivity)
            layoutManager = linearLayoutManager
        }

        topTextView.setOnClickListener {
            linearLayoutManager.scrollToPosition(0)
        }

        backTextView.setOnClickListener {
            val position = history.removeAt(history.size - 1)
            linearLayoutManager.scrollToPosition(position)
            if (history.isEmpty()) backTextView.gone()
        }

        if (app().ruleList.isLoaded()) loaded()
    }

    override fun loaded() {
        val adapter = RulesAdapter(this, app().ruleList).apply { rulesCallback = this@RulesActivity }
        runOnUiThread {
            progressBar.gone()
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            if (app().ruleList.isEmpty()) emptyText.visible()
        }
    }

    override fun scrollTo(position: Int) {
        backTextView.visible()
        history.add(linearLayoutManager.findFirstVisibleItemPosition())
        linearLayoutManager.scrollToPositionWithOffset(position, 0)
    }

    override fun browse(url: String) {
        startActivity {
            Intent(Intent.ACTION_VIEW).apply { data = Uri.parse("http://$url") }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntegerArrayList(HISTORY, ArrayList(history))
        super.onSaveInstanceState(outState)
    }
}