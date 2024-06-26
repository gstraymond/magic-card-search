package fr.gstraymond.ocr

import androidx.camera.view.PreviewView
import com.google.mlkit.vision.text.Text
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.network.ElasticSearchService
import fr.gstraymond.utils.levenshtein
import java.text.Normalizer
import kotlin.math.abs
import kotlin.math.min

class OcrDetectorProcessor(private val previewView: PreviewView,
                           private val cardDetector: CardDetector,
                           private val searchService: ElasticSearchService) {

    interface CardDetector {
        fun onTitleDetected(title: String)
        fun onCardDetected(card: Card)
        fun isPaused(): Boolean
    }

    private val convertedTypes = mapOf(
            "summon" to "creature",
            "artefact" to "artifact",
            "interrupt" to "instant",
            "ephemere" to "instant",
            "rituel" to "sorcery",
            "enchant" to "enchantment",
            "enchantement" to "enchantment",
            "terrain" to "land",
            "invoquer" to "creature")

    private val expectedTypes = listOf(
            "artifact", "interrupt", "instant", "sorcery", "creature", "enchant", "enchantment", "land", "summon", "planeswalker",
            "artefact", "ephemere", "rituel", "creature", "enchantement", "terrain", "invoquer")

    private fun queryTemplate(query: String, type: String) = """
{
  "query": {
    "bool": {
      "must": {
        "multi_match": {
          "fields": [
            "title",
            "frenchTitle"
          ],
          "query": "$query",
          "fuzziness": "AUTO"
        }
      },
      "filter": {
        "term": {
          "type": "$type"
        }
      }
    }
  }
}"""

    private val norm = Regex("""\p{M}""")

    fun receiveDetections(text: Text) {
        previewView.overlay.clear()
        if (cardDetector.isPaused()) return

        val allTextBlocks = text.textBlocks

        /*allTextBlocks.map {
            OcrGraphic(it, Color.RED)
        }.forEach {
            previewView.overlay.add(it)
        }*/


        val textBlocks = allTextBlocks.filter { it.lines.size == 1 }

        if (textBlocks.size > 1) {
            textBlocks.filter {
                it.text.length < 100 && normTypes(it).any(expectedTypes::contains)
            }.minByOrNull {
                it.boundingBox!!.top
            }?.let { detectedType ->
                // title is above type / title starts to the same left
                textBlocks
                        .filterNot { it == detectedType }
                        .filter { it.boundingBox!!.bottom < detectedType.boundingBox!!.top }
                        .minByOrNull { abs(it.boundingBox!!.left - detectedType.boundingBox!!.left) }
                        ?.let { detectedTitle ->

                            /*listOf(detectedTitle, detectedType).map {
                              OcrGraphic(it /* , Color.GREEN */)
                            }.forEach {
                                previewView.overlay.add(it)
                            }*/

                            val normTypes = normTypes(detectedType)
                            val writtenType = expectedTypes.find { normTypes.contains(it) }
                            val normalizedType = convertedTypes[writtenType!!] ?: writtenType

                            val trimmedTitle = detectedTitle.text.trim()

                            cardDetector.onTitleDetected(trimmedTitle)

                            val result = searchService.search(queryTemplate(trimmedTitle, normalizedType))
                            result?.elem?.hits?.hits?.map {
                                it to min(
                                        it._source.title.levenshtein(trimmedTitle),
                                        it._source.frenchTitle?.levenshtein(trimmedTitle) ?: Int.MAX_VALUE)
                            }?.filter {
                                it.second < 5
                            }?.minByOrNull {
                                it.second
                            }?.let {
                                cardDetector.onCardDetected(it.first._source)
                            }
                        }
            }
        }
    }

    private fun normTypes(it: Text.TextBlock): List<String> {
        return Normalizer.normalize(it.text.lowercase(), Normalizer.Form.NFD).replace(norm, "")
                .split(" ")
                .flatMap { it.split("-") }
                .map { it.replace(":", "") }
    }
}