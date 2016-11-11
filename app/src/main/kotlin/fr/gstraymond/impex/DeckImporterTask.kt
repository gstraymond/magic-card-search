package fr.gstraymond.impex

import android.content.ContentResolver
import android.net.Uri
import android.os.AsyncTask
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.db.json.Deck
import fr.gstraymond.db.json.Decklist
import fr.gstraymond.db.json.JsonDeck
import java.util.*

class DeckImporterTask(val contentResolver: ContentResolver,
                       val deckResolver: DeckResolver,
                       val jsonDeck: JsonDeck,
                       val decklist: Decklist,
                       val importerProcess: ImporterProcess) : AsyncTask<Uri, DeckImporterTask.Progress, Unit>() {

    data class Progress(val task: String, val result: Int)

    interface ImporterProcess {
        fun readUrl(nbCards: Int, result: Boolean)
        fun cardImported(card: String, result: Boolean)
        fun finished()
    }

    private val URL_TASK = "url_task"

    override fun doInBackground(vararg uris: Uri) {
        val importedDeck = DeckImporter(contentResolver).importFromUri(uris.first())
        publishProgress(Progress(URL_TASK, importedDeck?.lines?.size ?: -1))
        importedDeck?.let { deck ->
            val cards = deckResolver.resolve(deck, this)
            val deckId = decklist.getLastId() + 1
            jsonDeck.save("$deckId", cards)
            val deckStats = DeckStats(cards)
            decklist.addOrRemove(Deck(deckId, Date(), deck.name, deckStats.colors, deckStats.format))
        }
    }

    override fun onProgressUpdate(vararg values: Progress?) {
        super.onProgressUpdate(*values)
        values.filterNotNull().forEach {
            when (it.task) {
                URL_TASK -> importerProcess.readUrl(it.result, it.result > -1)
                else -> importerProcess.cardImported(it.task, it.result == 1)
            }
        }
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
        importerProcess.finished()
    }

    fun publishProgress(task: String, result: Boolean) {
        publishProgress(Progress(task, if (result) 1 else 0))
    }
}