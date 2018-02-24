package fr.gstraymond.search

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class TrieSpec : StringSpec() {
    init {
        "empty trie should return nothing" {
            Trie().get("a") shouldBe setOf<Int>()
        }
        "hello should return 0" {
            val trie = Trie()
            trie.add("hello", 0)
            trie.get("hello") shouldBe setOf(0)
        }
        "multiple hello should return 0, 2" {
            val trie = Trie()
            trie.add("hello", 0)
            trie.add("world", 1)
            trie.add("hello", 2)
            trie.get("hell") shouldBe setOf(0, 2)
            trie.get("hello") shouldBe setOf(0, 2)
        }
    }
}