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

    val formats = listOf(
            MTGODeckFormat(),
            MagicWorkstationDeckFormat())

    fun importFromUri(uri: Uri): ImportedDeck? {
        val deckList = openUrl(uri)
        log.d("decklist: \n$deckList")

        val lines = (deckList?.split("\n") ?: listOf())
                .map { it.replace("\r", "") }
                .filterNot(String::isBlank)

        return lines.run {
            if (isEmpty()) null
            else formats.find { it.detectFormat(lines) }?.let {
                val resolvedUri = when (uri.scheme) {
                    "content" -> resolveFileUri(uri) ?: uri
                    else -> uri
                }
                ImportedDeck(name = it.extractName(resolvedUri, lines),
                        lines = it.parse(lines))
            }
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
        }
    }

    private fun openUrl(uri: Uri): String? = when (uri.scheme) {
        "http", "https" -> fetchHTTP(uri)
        "content" -> fetchContent(uri)
        else -> null
    }

    private fun fetchHTTP(uri: Uri): String? =
            (URL(uri.toString()).openConnection() as HttpURLConnection).run {
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

    private fun fetchContent(uri: Uri): String? =
            contentResolver
                    .openInputStream(uri)
                    .bufferedReader()
                    .use { it.readText() }
}
