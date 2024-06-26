package fr.gstraymond.models.search.request.facet

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Facet(val terms: Term) {
    companion object {
        fun fromField(field: String) = Facet(Term(field))
    }

    override fun toString() = "Facet[$terms}"
}

@JsonClass(generateAdapter = true)
data class Term(val field: String, var size: Int? = null) {
    override fun toString() = "Term$field;${size ?: "NA"}"
}