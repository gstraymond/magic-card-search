package fr.gstraymond.utils

import android.os.Parcel
import java.util.*

object FacetParcelableUtils {
    fun readFacets(source: Parcel): Map<String, List<String>> {
        return readFacets(source.readString())
    }

    fun writeFacets(dest: Parcel, facets: Map<String, List<String>>) {
        dest.writeString(writeFacets(facets))
    }

    fun readFacets(facetsAsString: String?): Map<String, List<String>> {
        val facets = HashMap<String, List<String>>()

        if (facetsAsString == null || facetsAsString.isEmpty()) {
            return facets
        }

        val firstSplit = facetsAsString.split("\\|".toRegex())
        firstSplit.forEach { facet ->
            val keyValues = facet.split("=".toRegex())
            val key = keyValues[0]
            val valuesAsString = keyValues[1]
            facets.put(key, valuesAsString.split(",".toRegex()))
        }
        return facets
    }

    fun writeFacets(facets: Map<String, List<String>>): String {
        val facetsAsString = StringBuilder()
        var firstSep = ""
        for ((key, value) in facets) {
            facetsAsString.append(firstSep)
            facetsAsString.append(key)
            facetsAsString.append("=")
            var secondSep = ""
            value.forEach { v ->
                facetsAsString.append(secondSep)
                facetsAsString.append(v)
                secondSep = ","
            }
            firstSep = "|"
        }
        return facetsAsString.toString()
    }
}