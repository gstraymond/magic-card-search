package fr.gstraymond.impex

import android.content.ContentResolver
import android.os.AsyncTask
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.db.json.DeckList
import fr.gstraymond.db.json.CardListBuilder
import fr.gstraymond.models.Deck
import java.net.URL
import java.util.*

class DeckImporterTask(val contentResolver: ContentResolver,
                       val deckResolver: DeckResolver,
                       val cardListBuilder: CardListBuilder,
                       val deckList: DeckList,
                       val importerProcess: ImporterProcess) : AsyncTask<URL, DeckImporterTask.Progress, Unit>() {

    data class Progress(val task: String, val result: Int)

    interface ImporterProcess {
        fun readUrl(nbCards: Int, result: Boolean)
        fun cardImported(card: String, result: Boolean)
        fun finished()
    }

    private val URL_TASK = "url_task"

    override fun doInBackground(vararg urls: URL) {
        val importedDeck = DeckImporter(contentResolver).importFromUri(urls.first())
        publishProgress(Progress(URL_TASK, importedDeck?.lines?.size ?: -1))
        importedDeck?.let { deck ->
            val cards = deckResolver.resolve(deck, this)
            val deckId = deckList.getLastId() + 1
            cardListBuilder.build(deckId).save(cards)
            val deckStats = DeckStats(cards)
            deckList.addOrRemove(Deck(deckId, Date(), deck.name, deckStats.colors, deckStats.format))
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