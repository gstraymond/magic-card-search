package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.impex.DeckImporterTask
import fr.gstraymond.ui.adapter.DeckImporterAdapter
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.startActivity
import java.net.URL

class DeckImportProgressActivity : CustomActivity(R.layout.activity_deck_import_progress) {

    companion object {
        private val FILE_PATH = "FILE_PATH"

        fun getIntent(context: Context, filePath: String) =
                Intent(context, DeckImportProgressActivity::class.java).apply {
                    putExtra(FILE_PATH, filePath)
                }
    }

    private val logView by lazy { find<TextView>(R.id.deck_importer_log) }
    private val recyclerView by lazy { find<RecyclerView>(R.id.deck_importer_recyclerview) }
    private val button by lazy { find<Button>(R.id.deck_importer_button) }

    private val adapter by lazy { DeckImporterAdapter(this) }
    private val layoutManager by lazy { LinearLayoutManager(this) }

    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = layoutManager
            it.adapter = adapter
        }

        url = "file://${intent.getStringExtra(FILE_PATH)}"
        logView.text = "Importing ${url.split("/").last()}" // FIXME translate
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        DeckImporterTask(
                contentResolver,
                app().deckResolver,
                app().cardListBuilder,
                app().deckList,
                process
        ).execute(URL(url))
    }

    val process = object : DeckImporterTask.ImporterProcess {

        override fun readUrl(nbCards: Int, result: Boolean) {
            if (!result) {
                adapter.addLine(getString(R.string.bad_format))
                button.apply {
                    isEnabled = true
                    setText(R.string.go_back)
                    setOnClickListener { finish() }
                }
            }
        }

        override fun cardImported(card: String, result: Boolean) {
            if (result) {
                adapter.addLine(card)
                layoutManager.scrollToPosition(adapter.itemCount - 1)
            }
        }

        override fun finished(deckId: Int) {
            button.apply {
                isEnabled = true
                setText(R.string.go_to_deck)
                setOnClickListener {
                    startActivity {
                        DeckDetailActivity.getIntent(this@DeckImportProgressActivity, "$deckId")
                    }
                    finish()
                }
            }
        }
    }
}
