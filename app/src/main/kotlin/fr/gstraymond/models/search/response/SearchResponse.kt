package fr.gstraymond.models.search.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(val took: Int,
                        val hits: Hits,
                        val aggregations: Map<String, Aggregations>?)

@JsonClass(generateAdapter = true)
data class Hits(val total: Total,
                val hits: List<Hit>)

@JsonClass(generateAdapter = true)
data class Total(val value: Int)

@JsonClass(generateAdapter = true)
data class Hit(val _source: Card)

@JsonClass(generateAdapter = true)
data class Aggregations(val buckets: MutableList<Bucket>)

@JsonClass(generateAdapter = true)
data class Bucket(val key: String,
                  val doc_count: Int)