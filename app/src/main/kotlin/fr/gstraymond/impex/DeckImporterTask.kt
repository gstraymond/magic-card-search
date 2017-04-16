package fr.gstraymond.impex

import android.content.ContentResolver
import android.os.AsyncTask
import fr.gstraymond.biz.DeckManager
import fr.gstraymond.db.json.CardListBuilder
import fr.gstraymond.db.json.DeckList
import java.net.URL

class DeckImporterTask(val contentResolver: ContentResolver,
                       val deckResolver: DeckResolver,
                       cardListBuilder: CardListBuilder,
                       val deckList: DeckList,
                       val importerProcess: ImporterProcess) : AsyncTask<URL, DeckImporterTask.Progress, Int>() {

    data class Progress(val task: String, val result: Int)

    interface ImporterProcess {
        fun readUrl(nbCards: Int, result: Boolean)
        fun cardImported(card: String, result: Boolean)
        fun finished(deckId: Int)
    }

    private val URL_TASK = "url_task"

    private val deckManager = DeckManager(deckList, cardListBuilder)

    override fun doInBackground(vararg urls: URL): Int? {
        val importedDeck = DeckImporter(contentResolver).importFromUri(urls.first())
        publishProgress(Progress(URL_TASK, importedDeck?.lines?.size ?: -1))
        return importedDeck?.let { deck ->
            val cards = deckResolver.resolve(deck, this)
            deckManager.createDeck(deck.name, cards)
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