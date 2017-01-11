package fr.gstraymond.impex

class DeckParser {

    val formats = listOf(
            MTGODeckFormat(),
            MagicWorkstationDeckFormat())

    fun parse(deckList: String): Pair<DeckFormat, List<DeckLine>>? {
        println("DeckParser.parse:\n$deckList")
        val lines = deckList.split("\n")
                .map { it.replace("\r", "") }
                .map { it.dropWhile { it == ' ' } }
                .filterNot(String::isBlank)

        return lines.run {
            if (isEmpty()) null
            else formats
                    .find { it.detectFormat(lines) }
                    ?.run {
                        println("$this")
                        this to parse(lines)
                    }
        }
    }
}