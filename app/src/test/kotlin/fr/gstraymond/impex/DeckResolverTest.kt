package fr.gstraymond.impex

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.magic.card.search.commons.json.MapperUtil
import fr.gstraymond.network.ElasticSearchConnector
import fr.gstraymond.search.model.response.SearchResult
import org.junit.Assert.assertEquals
import org.junit.Test

class DeckResolverTest {

    /*@Test
    fun foo() {
        val connector = ElasticSearchConnector(
                "unit-test-${javaClass.canonicalName}",
                MapperUtil.fromType(getObjectMapper(), SearchResult::class.java))
        val deck = ImportedDeck(name = "foo",
                lines = listOf(
                        DeckLine(2, "Aetherworks Marvel", false),
                        DeckLine(2, "Dovin Baan", false),
                        DeckLine(15, "Plains", false)))
        val cards = DeckResolver(connector).resolve(deck, importerProcess, this)
        assertEquals(3, cards.size)
        assertEquals("Aetherworks Marvel", cards[0].card.title)
        assertEquals("Dovin Baan", cards[1].card.title)
        assertEquals("Plains", cards[2].card.title)
    }

    private fun getObjectMapper() =
            ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
  */
}