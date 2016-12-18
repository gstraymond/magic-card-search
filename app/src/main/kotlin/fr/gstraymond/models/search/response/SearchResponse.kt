package fr.gstraymond.models.search.response

data class SearchResult(val took: Int,
                        val hits: Hits,
                        val facets: Map<String, Facet>)

data class Hits(val total: Int,
                val hits: List<Hit>)

data class Hit(val _source: Card)

data class Facet(val terms: List<Term>)

data class Term(val term: String,
                val count: Int)