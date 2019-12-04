package fr.gstraymond.impex

import org.junit.Assert.*
import org.junit.Test
import java.net.URL

class DeckParserTest {

    @Test
    fun should_import_mtgo_format_decks() {
        testUrl("http://mtgtop8.com/mtgo?d=281503&f=Standard_Azorius_Aggro_by_Brad_Carpenter",
                "Standard_Azorius_Aggro_by_Brad_Carpenter",
                "Gideon, Ally of Zendikar")
    }

    @Test
    fun should_import_mtgo_format_decks_2() {
        testUrl("https://www.mtgdecks.net/decks/view/660419/txt",
                "txt",
                "Archangel Avacyn")
    }

    @Test
    fun should_import_mtgo_format_decks_3() {
        testUrl("https://www.mtggoldfish.com/deck/download/1884848",
                "1884848",
                "Acidic Slime",
                deckSize = 100,
                sideboardSize = 0)
    }

    @Test
    fun should_import_mtgo_format_decks_4() {
        testUrl("https://mtgdecks.net/Modern/affinity-analysis-3584/2017-01-20/txt",
                "txt",
                "Ornithopter",
                sideboardSize = 16)
    }

    @Test
    fun should_import_mtgo_format_decks_5() {
        testUrl("https://www.mtggoldfish.com/deck/download/615706",
                "615706",
                "Aether Hub")
    }

    @Test
    fun should_import_mtgo_format_decks_6() {
        testUrl("https://www.mtggoldfish.com/deck/download/625122",
                "625122",
                "Aether Hub")
    }

    @Test
    fun should_import_magic_workstation_format_decks() {
        testUrl("https://mtgtop8.com/dec?d=326201&f=Standard_Azorius_Aggro_by_5647382910",
                "Azorius Aggro",
                "Thopter Arrest")
    }

    @Test
    fun should_import_magic_workstation_format_decks_2() {
        testUrl("https://www.mtgdecks.net/decks/view/660419/dec",
                "Azorius Flash a Standard deck by Misplacedginger (dec) Version",
                "Archangel Avacyn")
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

        testDeck(deck,
                URL("http://www.mtgdecks.net/decks/view/660419/dec"),
                "dec",
                "Gideon, Ally of Zendikar")
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

        testDeck(deck,
                URL("http://www.mtgdecks.net/decks/view/660419/dec"),
                "Artifact (10)",
                "Animation Module",
                sideboardSize = 14)
    }


    @Test
    fun should_import_mtgarena_format_decks() {

        val deck = """4 Dragonskull Summit (XLN) 252
8 Swamp (RIX) 194
8 Mountain (RIX) 195
1 Midnight Reaper (GRN) 77
3 Gutterbones (RNA) 76
4 Priest of Forgotten Gods (RNA) 83
2 Rix Maadi Reveler (RNA) 109
4 Judith, the Scourge Diva (RNA) 185
3 Footlight Fiend (RNA) 216
4 Blood Crypt (RNA) 245
3 Lazotep Reaver (WAR) 96
2 Liliana, Dreadhorde General (WAR) 97
4 Grim Initiate (WAR) 130
4 Dreadhorde Butcher (WAR) 194
2 Ravenous Chupacabra (RIX) 82
4 Rekindling Phoenix (RIX) 111
"""

        testDeck(deck,
                URL("http://https://mtgadecks.net/deck/3380"),
                "3380",
                "Dragonskull Summit",
                sideboardSize = 0)
    }

    @Test
    fun should_import_mtgarena_format_decks_2() {
        val deck = """4 Concealed Courtyard (KLD) 245
2 Cultivator's Caravan (KLD) 203
1 Gideon of the Trials (AKH) 14
4 Gideon, Ally of Zendikar (BFZ) 29
4 Heart of Kiran (AER) 153
4 Inspiring Vantage (KLD) 246
4 Mountain (WAR) 261
6 Plains (WAR) 252
4 Scrapheap Scrounger (KLD) 231
1 Skysovereign, Consul Flagship (KLD) 234
2 Smoldering Marsh (BFZ) 247
1 Spire of Industry (AER) 184
1 Swamp (WAR) 258
4 Thraben Inspector (SOI) 44
4 Toolcraft Exemplar (KLD) 32
4 Unlicensed Disintegration (F17) 5
2 Veteran Motorist (KLD) 188
1 Ribbons (AKH) 223
3 Archangel Avacyn (V17) 1
3 Glorybringer (AKH) 134
2 Nahiri, the Harbinger (MED) WS7
3 Needle Spires (OGW) 175
"""
        testDeck(deck,
                URL("http://https://mtgadecks.net/deck/3381"),
                "3381",
                "Concealed Courtyard",
                deckSize = 64,
                sideboardSize = 0)
    }

    @Test
    fun should_import_mtgarena_format_decks_3() {
        val deck = """1 Aether Spellbomb (MMA) 196
1 Aetherflux Reservoir (KLD) 192
1 Altar of Dementia (MH1) 218
1 Altar of the Brood (KTK) 216
1 Arcane Denial (A25) 41
1 Ashnod's Altar (EMA) 218
1 Blinkmoth Urn (C18) 197
1 Blue Sun's Zenith (A25) 44
1 Cathodion (MM2) 203
1 Chakram Retriever (BBD) 15
1 Chromatic Sphere (MRD)
1 Codex Shredder (RTR)
1 Commander's Sphere (C19) 212
1 Corridor Monitor (ELD) 41
1 Darksteel Citadel (C18) 241
1 Diligent Excavator (DAR) 51
1 Echo Storm (C18) 7
1 Efficient Construction (AER) 33
1 Emry, Lurker of the Loch (ELD) 43
1 Etherium Sculptor (C18) 90
1 Everflowing Chalice (C16) 253
1 Foundry Inspector (KLD) 215
1 Freed from the Real (A25) 58
1 Ichor Wellspring (DDU) 54
34 Island (GN2) 57
1 Jace, Wielder of Mysteries (WAR) 54
1 Jhoira's Familiar (DAR) 220
1 Junk Diver (C14) 244
1 Krark-Clan Ironworks (5DN)
1 Laboratory Maniac (ISD)
1 Leyline of Anticipation (M20) 64
1 Lightning Greaves (C19) 217
1 Memnite (SOM) 174
1 Mind Stone (C18) 210
1 Mirran Spy (MBS)
1 Mirrodin Besieged (MH1) 57
1 Myr Moonvessel (DST)
1 Myr Retriever (C16) 264
1 Ornithopter (AER) 167
1 Padeem, Consul of Innovation (KLD) 59
1 Palladium Myr (IMA) 224
1 Reality Shift (C19) 92
1 Riddlesmith (DDU) 39
1 Sage of Lat-Nam (DAR) 64
1 Salvaging Station (5DN)
1 Scrap Trawler (AER) 175
1 Seat of the Synod (C18) 278
1 Secrets of the Dead (C19) 95
1 Semblance Anvil (SOM)
1 Shield Sphere (MED)
1 Silver Myr (SOM)
1 Sol Ring (C19) 221
1 Spellbook (M10)
1 Swan Song (C16) 98
1 Swiftfoot Boots (C18) 225
1 Tale's End (M20) 77
1 Thirst for Knowledge (C18) 106
1 Thopter Spy Network (C18) 107
1 Thornbite Staff (MOR)
1 Thoughtcast (MM2)
1 Tormod's Crypt (C14) 278
1 Tribute Mage (MH1) 73
1 Trinket Mage (DDU) 41
1 Trophy Mage (DDU) 42
1 Ugin, the Ineffable (WAR) 2
1 Voltaic Key (M11) 219
1 Wayfarer's Bauble (CM2) 229
"""
        testDeck(deck,
                URL("http://https://mtgadecks.net/deck/3381"),
                "3381",
                "Aether Spellbomb",
                deckSize = 100,
                sideboardSize = 0)
    }

    @Test
    fun should_handle_bad_url() {
        val uri = URL("http://google.com/test")
        val result = DeckParser().parse(uri.fetch() ?: "", uri, false)
        assertTrue("deck must be null", result == null)
    }

    @Test
    fun should_handle_image() {
        val url = URL("https://www.google.com/s2/favicons?domain=www.google.com")
        val result = DeckParser().parse(url.fetch() ?: "", url, false)
        assertTrue("deck must be null", result == null)
    }

    private fun testUrl(url: String, expectedName: String, firstCard: String, deckSize: Int = 60, sideboardSize: Int = 15) {
        println("testParseOk url $url")
        testDeck(URL(url).fetch() ?: "", URL(url), expectedName, firstCard, deckSize, sideboardSize)
    }

    private fun testDeck(deck: String, url: URL,
                         expectedName: String,
                         firstCard: String,
                         deckSize: Int = 60,
                         sideboardSize: Int = 15) {
        println("testDeck deck:\n$deck")
        DeckParser().parse(deck, url, false)?.run {
            println("testDeck parsed:\n$this")
            assertEquals(deckSize, lines.filterNot { it.isSideboard }.map { it.mult }.sum())
            assertEquals(sideboardSize, lines.filter { it.isSideboard }.map { it.mult }.sum())
            assertEquals(expectedName, name)
            assertEquals(firstCard, lines.first().title)
        } ?: fail("unable to parse deck")
    }
}