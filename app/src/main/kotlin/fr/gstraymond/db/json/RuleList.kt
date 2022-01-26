package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.models.search.response.Rule
import fr.gstraymond.search.Trie


class RuleList(context: Context,
               moshi: Moshi) : LazyJsonList<Rule>(
        context,
        MapperUtil.fromType(moshi, Rule::class.java),
        listName = "rule") {
    override fun Rule.uid() = ""

    val trie = Trie()

    override fun setLoaded() {
        val rangeSize = 2..15
        all().withIndex().forEach { (index, rule) ->
            rule.text
                    .lowercase()
                    .split(" ")
                    .map { it.filter { it1 -> it1.isLetterOrDigit() } }
                    .filter { rangeSize.contains(it.length) }
                    .forEach { trie.add(it, index) }
        }
        super.setLoaded()
    }
}