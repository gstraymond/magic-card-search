package fr.gstraymond.impex

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.net.URL

class DeckParserTest {

    @Test
    fun should_import_mtgo_format_decks() {
        testUrl("http://mtgtop8.com/mtgo?d=281503&f=Standard_Azorius_Aggro_by_Brad_Carpenter")
    }

    @Test
    fun should_import_mtgo_format_decks_2() {
        testUrl("http://www.mtgdecks.net/decks/view/660419/txt")
    }

    @Test
    fun should_import_mtgo_format_decks_3() {
        testUrl("https://www.mtggoldfish.com/deck/download/542503", 100, 0)
    }

    @Test
    fun should_import_magic_workstation_format_decks() {
        testUrl("http://mtgtop8.com/export_files/deck281503.mwDeck")
    }

    @Test
    fun should_import_magic_workstation_format_decks_2() {
        testUrl("http://www.mtgdecks.net/decks/view/660419/dec")
    }

    @Test
    fun should_import_magic_wizard_format_decks() {

        val deck = """2 Gideon, Ally of Zendikar
4 Thraben Inspector
4 Toolcraft Exemplar
3 Inventor's Apprentice
4 Veteran Motorist
3 Thalia, Heretic Cathar
4 Scrapheap Scrounger
3 Harnessed Lightning
4 Unlicensed Disintegration
4 Smuggler's Copter
3 Cultivator's Caravan
4 Inspiring Vantage
4 Aether Hub
4 Concealed Courtyard
4 Spirebluff Canal
2 Mountain
4 Plains


2 Gideon, Ally of Zendikar
4 Ceremonious Rejection
1 Fragmentize
2 Skysovereign, Consul Flagship
4 Galvanic Bombardment
2 Declaration in Stone"""

        testDeck(deck)
    }

    @Test
    fun should_import_mtgvault_com_format_decks() {

        val deck = """//Artifact (10)
2 Animation Module
2 Decoction Module
2 Fabrication Module
4 Smuggler's Copter

//Artifact Creature (16)
4 Filigree Familiar
4 Hedron Crawler
2 Multiform Wonder
2 Noxious Gearhulk
4 Scrapheap Scrounger

//Creature (6)
2 Marionette Master
4 Syndicate Trafficker

//Instant (6)
4 Grasp of Darkness
2 Murder

//Land (22)
4 Aether Hub
2 Inventors' Fair
16 Swamp

SB: 4 Harsh Scrutiny
SB: 2 Murder
SB: 4 Ruinous Path
SB: 4 Transgress the Mind"""

        testDeck(deck, sideboardSize = 14)
    }

    @Test
    fun should_handle_bad_url() {
        val url = URL("http://google.com/test")
        val deckList = url.fetch() ?: ""
        println("should_handle_bad_url: $deckList")
        val result = DeckParser().parse(deckList)
        println("should_handle_bad_url: $result")
        assertTrue("deck must be null", result == null)
    }

    @Test
    fun should_handle_image() {
        val url = URL("https://www.google.com/s2/favicons?domain=www.google.com")
        val result = DeckParser().parse(url.fetch() ?: "")
        assertTrue("deck must be null", result == null)
    }

    private fun testUrl(url: String, deckSize: Int = 60, sideboardSize: Int = 15) {
        println("testParseOk url $url")
        testDeck(URL(url).fetch() ?: "", deckSize, sideboardSize)
    }

    private fun testDeck(deck: String, deckSize: Int = 60, sideboardSize: Int = 15) {
        println("testDeck deck\n$deck")
        DeckParser().parse(deck)?.run {
            val lines = second
            assertEquals(deckSize, lines.filterNot { it.isSideboard }.map { it.occurrence }.sum())
            assertEquals(sideboardSize, lines.filter { it.isSideboard }.map { it.occurrence }.sum())
        } ?: fail()
    }
}