package fr.gstraymond.impex

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.magic.card.search.commons.log.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class ImportedDeck(val name: String, val lines: List<DeckLine>)

data class DeckLine(val occurrence: Int, val title: String, val isSideboard: Boolean)

class DeckImporter(private val contentResolver: ContentResolver) {

    private val log = Log(this)

    private val deckParser = DeckParser()

    fun importFromUri(uri: Uri): ImportedDeck? {
        val deckList = openUrl(uri)
        log.d("decklist: \n$deckList")
        return deckList?.run { parse(this, uri) }
    }

    private fun parse(deckList: String, uri: Uri): ImportedDeck? {
        return deckParser.parse(deckList)?.run {
            val (deckFormat, lines) = this
            val resolvedUri = when (uri.scheme) {
                "content" -> resolveFileUri(uri) ?: uri
                else -> uri
            }
            ImportedDeck(
                    name = deckFormat.extractName(resolvedUri, deckList.split("\n")),
                    lines = lines)
        }?.apply {
            log.d("imported: ")
            log.d(name)
            log.d(this.lines.joinToString("\n"))
        }
    }

    private val proj = arrayOf(MediaStore.Images.Media.TITLE)

    private fun resolveFileUri(uri: Uri) = contentResolver.query(uri, proj, null, null, null)?.run {
        if (count != 0) {
            moveToFirst()
            val resolvedUri = Uri.parse("content://downloads/${getString(getColumnIndexOrThrow(proj.first()))}")
            close()
            resolvedUri
        } else {
            null
        }.apply {
            close()
        }
    }

    private fun openUrl(uri: Uri): String? = when (uri.scheme) {
        "http", "https" -> fetchHTTP(uri)
        "content" -> fetchContent(uri)
        else -> null
    }

    private fun fetchHTTP(uri: Uri): String? = uri.fetch()

    private fun fetchContent(uri: Uri): String? =
            contentResolver
                    .openInputStream(uri)
                    .bufferedReader()
                    .use { it.readText() }
}

fun Uri.fetch(): String? = URL(toString()).fetch()

fun URL.fetch(): String? = (openConnection() as HttpURLConnection).run {
    try {
        setRequestProperty("connection", "close")
        inputStream.bufferedReader().use { it.readText() }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    } finally {
        disconnect()
    }
}