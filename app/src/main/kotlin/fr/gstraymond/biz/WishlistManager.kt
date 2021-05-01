package fr.gstraymond.biz

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import fr.gstraymond.db.json.WishList
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.ImportResult
import java.nio.charset.Charset

class WishlistManager(val wishlist: WishList) {

    fun export(path: Uri, contentResolver: ContentResolver, context: Context): String {
        contentResolver.openOutputStream(path)!!.writer(Charset.defaultCharset()).use {
            wishlist.all().forEach { card -> it.write(card.title + "\n") }
        }
        return DocumentFile.fromSingleUri(context, path)?.name ?: ""
    }

    fun replace(cards: List<ImportResult>) {
        wishlist.clear()
        cards.forEach {
            when (it) {
                is DeckLine -> wishlist.addOrRemove(it.card)
            }
        }
    }
}