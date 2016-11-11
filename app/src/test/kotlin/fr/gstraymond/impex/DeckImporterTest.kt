package fr.gstraymond.impex

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.MalformedURLException
import java.net.URL

class DeckImporterTest {

    @Test
    @Throws(MalformedURLException::class)
    fun should_import_mtgo_format_decks() {
        val url = URL("http://mtgtop8.com/mtgo?d=281503&f=Standard_Azorius_Aggro_by_Brad_Carpenter")
        val deck = DeckImporter.importFromUrl(url)
        assertTrue("deck must not be null", deck != null)
        assertEquals("Standard_Azorius_Aggro_by_Brad_Carpenter", deck!!.name)
        assertEquals(60, deck.lines.filterNot { it.isSideboard }.map { it.occurrence }.sum())
        assertEquals(15, deck.lines.filter { it.isSideboard }.map { it.occurrence }.sum())
    }

    @Test
    @Throws(MalformedURLException::class)
    fun should_import_magic_workstation_format_decks() {
        val url = URL("http://mtgtop8.com/export_files/deck281503.mwDeck")
        val deck = DeckImporter.importFromUrl(url)
        assertTrue("deck must not be null", deck != null)
        assertEquals("Azorius Aggro", deck!!.name)
        assertEquals(60, deck.lines.filterNot { it.isSideboard }.map { it.occurrence }.sum())
        assertEquals(15, deck.lines.filter { it.isSideboard }.map { it.occurrence }.sum())
    }

    @Test
    @Throws(MalformedURLException::class)
    fun should_handle_bad_url() {
        val url = URL("http://google.com/test")
        val deck = DeckImporter.importFromUrl(url)
        assertTrue("deck must not be null", deck == null)
    }

    @Test
    @Throws(MalformedURLException::class)
    fun should_handle_image() {
        val url = URL("https://www.google.com/s2/favicons?domain=www.google.com")
        val deck = DeckImporter.importFromUrl(url)
        assertTrue("deck must not be null", deck == null)
    }
}
