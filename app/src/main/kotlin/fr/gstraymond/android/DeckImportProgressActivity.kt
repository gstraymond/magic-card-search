package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.gstraymond.R
import fr.gstraymond.impex.DeckImporterTask
import fr.gstraymond.ui.adapter.DeckImporterAdapter
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.startActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class DeckImportProgressActivity : CustomActivity(R.layout.activity_deck_import_progress) {

    companion object {
        private const val FILE_PATH = "FILE_PATH"
        private const val DECK_LIST = "DECK_LIST"
        private const val FORMAT = "FORMAT"
        private const val WISHLIST = "WISHLIST"

        const val WISHLIST_RESULT = -1

        fun getIntent(context: Context, deckList: String, maybeFormat: String?, wishlist: Boolean) =
                Intent(context, DeckImportProgressActivity::class.java).apply {
                    putExtra(DECK_LIST, deckList)
                    putExtra(WISHLIST, wishlist)
                    maybeFormat?.let { putExtra(FORMAT, it) }
                }

        fun getIntent(context: Context, path: Uri) =
                Intent(context, DeckImportProgressActivity::class.java).apply {
                    putExtra(FILE_PATH, path.toString())
                    putExtra(WISHLIST, false)
                }
    }

    private val logView by lazy { find<TextView>(R.id.deck_importer_log) }
    private val recyclerView by lazy { find<RecyclerView>(R.id.deck_importer_recyclerview) }
    private val button by lazy { find<Button>(R.id.deck_importer_button) }

    private val adapter by lazy { DeckImporterAdapter(this) }
    private val layoutManager by lazy { LinearLayoutManager(this) }

    private lateinit var deckAsString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = layoutManager
            it.adapter = adapter
        }

        deckAsString = intent.getStringExtra(FILE_PATH)?.run {
            val stream = contentResolver.openInputStream(Uri.parse(this))
            val reader = BufferedReader(InputStreamReader(stream))
            val total = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                total.append(line).append('\n')
            }
            logView.text = String.format(getString(R.string.import_deck), this.split("/").last())
            total.toString()
        } ?: intent.getStringExtra(DECK_LIST)!!.apply {
            logView.text = getString(R.string.refreshing)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        DeckImporterTask(
                contentResolver,
                app().deckResolver,
                app().deckManager,
                app().wishlistManager,
                process,
                intent.getStringExtra(FORMAT),
                intent.getBooleanExtra(WISHLIST, false)
        ).execute(deckAsString)
    }

    private val process = object : DeckImporterTask.ImporterProcess {

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
            if (result) adapter.addLine(card)
            else adapter.addLine("""<font color="red">$card -> KO</font>""")

            layoutManager.scrollToPosition(adapter.itemCount - 1)
        }

        override fun finished(deckId: Int) {
            button.apply {
                isEnabled = true
                when (deckId) {
                    WISHLIST_RESULT -> {
                        setText(R.string.close)
                        setOnClickListener { finish() }
                    }
                    else -> {
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
    }
}
