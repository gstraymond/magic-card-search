/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.gstraymond.ocr

import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.network.ElasticSearchService
import fr.gstraymond.ocr.ui.camera.GraphicOverlay
import fr.gstraymond.utils.levenshtein
import java.text.Normalizer

class OcrDetectorProcessor(private val graphicOverlay: GraphicOverlay<OcrGraphic>,
                           private val cardDetector: CardDetector,
                           private val searchService: ElasticSearchService) : Detector.Processor<TextBlock> {

    interface CardDetector {
        fun onCardDetected(card: Card)
        fun isPaused(): Boolean
    }

    private val expectedTypes = listOf(
            "artifact", "instant", "sorcery", "creature", "enchant", "enchantment", "land", "summon", "planeswalker",
            "artefact", "ephemere", "rituel", "creature", "enchantement", "terrain", "invoquer")

    private val convertedTypes = mapOf(
            "summon" to "creature",
            "artefact" to "artifact",
            "ephemere" to "instant",
            "rituel" to "sorcery",
            "enchant" to "enchantment",
            "enchantement" to "enchantment",
            "terrain" to "land",
            "invoquer" to "creature")

    private val queryTemplate = """
{
  "query": {
    "filtered": {
      "query": {
        "multi_match": {
          "fields": [
            "title",
            "frenchTitle"
          ],
          "query": "QUERY_ARG",
          "fuzziness": "AUTO"
        }
      },
      "filter": {
        "term": {
          "type": "TYPE_ARG"
        }
      }
    }
  }
}"""

    private val norm = Regex("\\p{M}")

    override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
        graphicOverlay.clear()
        if (cardDetector.isPaused()) return

        val items = detections.detectedItems
        val textBlocks = (0..items.size() - 1).map { items.valueAt(it) }.filter { it.components.size == 1 }

        if (textBlocks.size > 1) {
            textBlocks.filter {
                normTypes(it).any { expectedTypes.contains(it) }
            }.minBy {
                it.boundingBox.top
            }?.let { detectedType ->
                // title is above type / title starts to the same left
                val detectedTitle = textBlocks
                        .filterNot { it == detectedType }
                        .filter { it.boundingBox.bottom < detectedType.boundingBox.top }
                        .sortedBy { Math.abs(it.boundingBox.left - detectedType.boundingBox.left) }
                        .first()



                listOf(detectedTitle, detectedType).map {
                    OcrGraphic(graphicOverlay, it)
                }.forEach {
                    graphicOverlay.add(it)
                }

                val normTypes = normTypes(detectedType)
                val writtenType = expectedTypes.find { normTypes.contains(it) }
                val normalizedType = convertedTypes.getOrDefault(writtenType!!, writtenType)

                val trimmedTitle = detectedTitle.value.trim()
                val result = searchService.search(queryTemplate.replace("QUERY_ARG", trimmedTitle).replace("TYPE_ARG", normalizedType))
                result?.elem?.hits?.hits?.map {
                    it to Math.min(
                            it._source.title.levenshtein(trimmedTitle),
                            it._source.frenchTitle?.levenshtein(trimmedTitle) ?: Int.MAX_VALUE)
                }?.filter {
                    it.second < 5
                }?.minBy {
                    it.second
                }?.let {
                    cardDetector.onCardDetected(it.first._source)
                }
            }
        }
    }

    private fun normTypes(it: TextBlock): List<String> {
        return Normalizer.normalize(it.value.toLowerCase(), Normalizer.Form.NFD).replace(norm, "")
                .split(" ")
                .flatMap { it.split("-") }
                .map { it.replace(":", "") }
    }

    override fun release() {
        graphicOverlay.clear()
    }
}