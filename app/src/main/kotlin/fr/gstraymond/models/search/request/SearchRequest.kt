package fr.gstraymond.models.search.request

import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.constants.FacetConst
import fr.gstraymond.models.search.request.facet.Facet

data class Request(val query: Query,
                   val from: Int,
                   val size: Int,
                   val facets: Map<String, Facet>,
                   val sort: Map<String, Order>) {
    companion object {
        fun fromOptions(options: SearchOptions) =
                Request(query = Query.fromOptions(options),
                        from = options.from,
                        size = options.size,
                        facets = when (options.append) {
                            true -> mapOf()
                            else -> FacetConst.getFacets().apply {
                                options.facetSize.forEach { facetSize ->
                                    get(facetSize.key)?.terms?.size = facetSize.value
                                }
                            }
                        },
                        sort = when  {
                            options.sort != null -> {
                                val split = options.sort!!.split(":")
                                mapOf(split[0] to Order(split[1]))
                            }
                            SearchOptions.QUERY_ALL == options.query && !options.random -> mapOf("_uid" to Order())
                            else -> mapOf()
                        }
                )
    }
}

data class Query(val query_string: Query_string?,
                 val bool: Bool?) {
    companion object {
        fun fromOptions(options: SearchOptions) =
                Query(query_string = null,
                        bool = Bool.fromOptions(options))
    }
}

data class Query_string(val query: String,
                        val default_operator: String = "AND")

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

data class QueryString(val query_string: Query_string)

data class FunctionScore(val function_score: Function_score = Function_score())

data class Function_score(val query: QueryMatchAll = QueryMatchAll(),
                          val random_score: Any = object {})

data class QueryMatchAll(val match_all: Any = object {})

data class Term(val term: Map<String, String>)

data class Order(var order: String = "asc")