package fr.gstraymond.models.search.request

import com.squareup.moshi.JsonClass
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.constants.FacetConst
import fr.gstraymond.models.search.request.facet.Facet

@JsonClass(generateAdapter = true)
data class Request(val query: Query,
                   val from: Int,
                   val size: Int,
                   val aggregations: Map<String, Facet>,
                   val sort: Map<String, Order>,
                   val track_total_hits: Boolean = true) {
    companion object {
        fun fromOptions(options: SearchOptions) =
                Request(query = Query.fromOptions(options),
                        from = options.from,
                        size = options.size,
                        aggregations = when (options.append) {
                            true -> mapOf()
                            else -> FacetConst.getFacets().apply {
                                options.facetSize.forEach { facetSize ->
                                    get(facetSize.key)?.terms?.size = facetSize.value
                                }
                            }
                        },
                        sort = when {
                            options.sort != null -> {
                                options.sort!!.split(",").map {
                                    it.split(":").let { it1 -> it1[0] to Order(it1[1]) }
                                }.toMap()
                            }
                            SearchOptions.QUERY_ALL == options.query && !options.random -> mapOf("_id" to Order())
                            else -> mapOf()
                        }
                )
    }
}

@JsonClass(generateAdapter = true)
data class Query(val query_string: Query_string?,
                 val bool: Bool?) {
    companion object {
        fun fromOptions(options: SearchOptions) =
                Query(query_string = null,
                        bool = Bool.fromOptions(options))
    }
}

@JsonClass(generateAdapter = true)
data class Query_string(val query: String,
                        val default_operator: String = "AND")

@JsonClass(generateAdapter = true)
data class Bool(val must: List<Any>) {
    companion object {
        fun fromOptions(options: SearchOptions): Bool {
            val must = mutableListOf<Any>()
            must.add(when (options.random) {
                true -> FunctionScore()
                else -> QueryString(Query_string(options.query))
            })
            options.facets.forEach { facet ->
                facet.value.forEach { term ->
                    must.add(Term(mapOf(facet.key to term)))
                }
            }
            return Bool(must = must)
        }
    }
}

@JsonClass(generateAdapter = true)
data class QueryString(val query_string: Query_string)

@JsonClass(generateAdapter = true)
data class FunctionScore(val function_score: Function_score = Function_score())

@JsonClass(generateAdapter = true)
data class Function_score(val query: QueryMatchAll = QueryMatchAll(),
                          val random_score: Map<String,String> = HashMap())

@JsonClass(generateAdapter = true)
data class QueryMatchAll(val match_all: Map<String,String> = HashMap())

@JsonClass(generateAdapter = true)
data class Term(val term: Map<String, String>)

@JsonClass(generateAdapter = true)
data class Order(var order: String = "asc")