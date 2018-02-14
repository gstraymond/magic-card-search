package fr.gstraymond.search

data class Trie(private val char: Char,
                private val indices: MutableList<Int> = mutableListOf(),
                private val children: MutableList<Trie> = mutableListOf()) {

    fun add(word: String,
            index: Int) {
        val first = word.first()
        val trie = findOrCreate(first)

        when (word.length) {
            1 -> trie.indices.add(index)
            else -> trie.add(word.drop(1), index)
        }
    }

    fun get(word: String): List<Int> {
        val first = word.first()

        return find(first)?.run {
            when (word.length) {
                1 -> indices
                else -> get(word.drop(1))
            }
        } ?: listOf()
    }

    private fun findOrCreate(char: Char) =
            find(char) ?: Trie(char).apply { this@Trie.children.add(this) }

    private fun find(char: Char) =
            children.find { it.char == char }
}

object TrieBuilder {
    fun empty() = Trie(char = ' ')
}