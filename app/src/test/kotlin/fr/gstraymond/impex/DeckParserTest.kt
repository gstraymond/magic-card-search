package fr.gstraymond.impex

import org.junit.Assert.*
import org.junit.Test
import java.net.MalformedURLException
import java.net.URL

class DeckParserTest {

    @Test
    fun should_import_mtgo_format_decks() {
        testParseOk("http://mtgtop8.com/mtgo?d=281503&f=Standard_Azorius_Aggro_by_Brad_Carpenter")
    }

    @Test
    fun should_import_mtgo_format_decks_2() {
        testParseOk("http://www.mtgdecks.net/decks/view/660419/txt")
    }

    @Test
    fun should_import_magic_workstation_format_decks() {
        testParseOk("http://mtgtop8.com/export_files/deck281503.mwDeck")
    }

    @Test
    fun should_import_magic_workstation_format_decks_2() {
        testParseOk("http://www.mtgdecks.net/decks/view/660419/dec")
    }

    @Test
    fun should_handle_bad_url() {
        val url = URL("http://google.com/test")
        val result = DeckParser().parse(url.fetch() ?: "")
        assertTrue("deck must be null", result == null)
    }

    @Test
    fun should_handle_image() {
        val url = URL("https://www.google.com/s2/favicons?domain=www.google.com")
        val result = DeckParser().parse(url.fetch() ?: "")
        assertTrue("deck must be null", result == null)
    }

    private fun testParseOk(url: String) {
        println("testParseOk url $url")
        DeckParser().parse(URL(url).fetch() ?: "")?.run {
            val lines = second
            assertEquals(60, lines.filterNot { it.isSideboard }.map { it.occurrence }.sum())
            assertEquals(15, lines.filter { it.isSideboard }.map { it.occurrence }.sum())
        } ?: fail()
    }
}
