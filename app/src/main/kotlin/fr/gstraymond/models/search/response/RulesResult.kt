package fr.gstraymond.models.search.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RulesResult(val _source: RulesSource)

@JsonClass(generateAdapter = true)
data class RulesSource(val filename: String,
                       val rules: List<Rule>?)

@JsonClass(generateAdapter = true)
data class Rule(val id: String?,
                val text: String,
                val links: List<RuleLink>,
                val level: Int)

@JsonClass(generateAdapter = true)
data class RuleLink(val id: String,
                    val start: Int,
                    val end: Int)