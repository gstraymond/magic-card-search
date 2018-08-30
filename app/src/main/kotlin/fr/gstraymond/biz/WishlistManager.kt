package fr.gstraymond.biz

import fr.gstraymond.db.json.WishList
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.ImportResult
import java.io.File

class WishlistManager(val wishlist: WishList) {

    fun export(path: String): String {
        val files = File(path).listFiles().map { it.name }
        val filename = findUniqueName(files)
        val targetPath = "$path/$filename"
        File(targetPath).printWriter().use {
            wishlist.all().forEach { card -> it.write(card.title + "\n") }
        }
        return targetPath
    }

    private fun findUniqueName(files: List<String>,
                               deckName: String = "wishlist"): String {
        val targetName = "$deckName.txt"
        return if (files.contains(targetName)) {
            if (deckName.last().isDigit() && deckName.contains("_")) {
                val rootName = deckName.dropLastWhile { it != '_' }
                val counter = deckName.takeLastWhile { it != '_' }.toInt() + 1
                findUniqueName(files, "$rootName$counter")
            } else findUniqueName(files, "${deckName}_1")
        } else {
            targetName
        }
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