package fr.gstraymond.search

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class TrieSpec : StringSpec() {
    init {
        "empty trie should return nothing" {
            TrieBuilder.empty().get("a") shouldBe listOf<Int>()
        }
        "hello should return 0" {
            val emptyTrie = TrieBuilder.empty()
            emptyTrie.add("hello", 0)
            emptyTrie.get("hello") shouldBe listOf(0)
        }
        "multiple hello should return 0, 2" {
            val emptyTrie = TrieBuilder.empty()
            emptyTrie.add("hello", 0)
            emptyTrie.add("world", 1)
            emptyTrie.add("hello", 2)
            emptyTrie.get("hello") shouldBe listOf(0, 2)
        }
    }
}