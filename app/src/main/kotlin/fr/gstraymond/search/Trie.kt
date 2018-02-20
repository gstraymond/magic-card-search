package fr.gstraymond.search

data class Trie(private val indices: MutableSet<Int> = mutableSetOf(),
                private val children: MutableMap<Char, Trie> = mutableMapOf()) {

    fun add(word: String,
            index: Int) {
        val trie = findOrCreate(word.first())
        when (word.length) {
            1 -> trie.indices.add(index)
            else -> trie.add(word.drop(1), index)
        }
    }

    fun get(word: String): Set<Int> =
            children[word.first()]?.run {
                when (word.length) {
                    1 -> indices
                    else -> get(word.drop(1))
                }
            } ?: setOf()

    private fun findOrCreate(char: Char) =
            children[char] ?: Trie().apply { this@Trie.children[char] = this }

}