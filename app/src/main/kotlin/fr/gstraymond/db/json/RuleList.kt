package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.models.search.response.Rule


class RuleList(context: Context,
               moshi: Moshi) : LazyJsonList<Rule>(
        context,
        MapperUtil.fromType(moshi, Rule::class.java),
        listName = "rule") {
    override fun Rule.uid() = ""
}