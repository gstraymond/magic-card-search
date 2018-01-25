package fr.gstraymond.models.search.response

data class RulesResult(val _source: RulesSource)

data class RulesSource(val filename: String,
                       val rules: List<Rule>?)

data class Rule(val id: String?,
                val text: String,
                val links: List<RuleLink>,
                val level: Int)

data class RuleLink(val id: String,
                    val start: Int,
                    val end: Int)