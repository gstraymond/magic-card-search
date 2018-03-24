package fr.gstraymond.impex

import android.content.ContentResolver
import android.os.AsyncTask
import fr.gstraymond.biz.DeckManager
import java.net.URL

class DeckImporterTask(private val contentResolver: ContentResolver,
                       private val deckResolver: DeckResolver,
                       private val deckManager: DeckManager,
                       private val importerProcess: ImporterProcess,
                       private val maybeFormat: String?) : AsyncTask<String, DeckImporterTask.Progress, Int>() {

    data class Progress(val task: String, val result: Int)

    interface ImporterProcess {
        fun readUrl(nbCards: Int, result: Boolean)
        fun cardImported(card: String, result: Boolean)
        fun finished(deckId: Int)
    }

    private val URL_TASK = "url_task"

    override fun doInBackground(vararg strings: String): Int? {
        val string = strings.first()
        val importedDeck = when {
            string.startsWith("file:") -> DeckImporter(contentResolver).importFromUri(URL(string))
            else -> DeckImporter(contentResolver).importFromText(string)
        }
        publishProgress(Progress(URL_TASK, importedDeck?.lines?.size ?: -1))
        return importedDeck?.let { deck ->
            val cards = deckResolver.resolve(deck, this)
            deckManager.createDeck(deck.name, cards, maybeFormat)
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

    override fun onPostExecute(result: Int?) {
        super.onPostExecute(result)
        result?.apply {
            importerProcess.finished(this)
        }

    }

    fun publishProgress(task: String, result: Boolean) {
        publishProgress(Progress(task, if (result) 1 else 0))
    }
}