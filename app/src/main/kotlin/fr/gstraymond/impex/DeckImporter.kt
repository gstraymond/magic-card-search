package fr.gstraymond.impex

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.magic.card.search.commons.log.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class ImportedDeck(val name: String, val lines: List<DeckTextLine>)

data class DeckTextLine(val mult: Int, val title: String, val isSideboard: Boolean)

class DeckImporter(private val contentResolver: ContentResolver) {

    private val log = Log(this)

    private val deckParser = DeckParser()

    fun importFromUri(url: URL): ImportedDeck? {
        val deckList = openUrl(url)
        log.d("decklist: \n$deckList")
        return deckList?.run { parse(this, url) }
    }

    private fun parse(deckList: String, url: URL): ImportedDeck? {
        val resolvedUri = when (url.protocol) {
            "fle" -> resolveFileURL(url)
            else -> null
        } ?: url

        return deckParser.parse(deckList, resolvedUri)?.apply {
            log.d("imported: ")
            log.d(name)
            log.d(lines.joinToString("\n"))
        }
    }

    private val proj = arrayOf(MediaStore.Images.Media.TITLE)

    private fun resolveFileURL(url: URL) = contentResolver.query(Uri.parse(url.toString()), proj, null, null, null)?.run {
        if (count != 0) {
            moveToFirst()
            val resolvedUri = URL("content://downloads/${getString(getColumnIndexOrThrow(proj.first()))}")
            close()
            resolvedUri
        } else {
            null
        }.apply {
            close()
        }
    }

    private fun openUrl(url: URL): String? = when (url.protocol) {
        "http", "https" -> fetchHTTP(url)
        "file" -> fetchContent(url)
        else -> null
    }

    private fun fetchHTTP(url: URL): String? = url.fetch()

    private fun fetchContent(url: URL): String? =
            contentResolver
                    .openInputStream(Uri.parse(url.toString()))
                    .bufferedReader()
                    .use { it.readText() }
}

fun Uri.fetch(): String? = URL(toString()).fetch()

fun URL.fetch(): String? = (openConnection() as HttpURLConnection).run {
    try {
        setRequestProperty("connection", "close")
        inputStream.bufferedReader().use { it.readText() }
    } catch (e: IOException) {
        Log(this).w("fetch: ${e.message}")
        null
    } finally {
        disconnect()
    }
}