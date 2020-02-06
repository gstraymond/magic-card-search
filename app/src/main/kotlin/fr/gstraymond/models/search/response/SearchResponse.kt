package fr.gstraymond.models.search.response

data class SearchResult(val took: Int,
                        val hits: Hits,
                        val aggregations: Map<String, Aggregations>)

data class Hits(val total: Total,
                val hits: List<Hit>)

data class Total(val value: Int)

data class Hit(val _source: Card)

data class Aggregations(val buckets: MutableList<Bucket>)

data class Bucket(val key: String,
                  val doc_count: Int)