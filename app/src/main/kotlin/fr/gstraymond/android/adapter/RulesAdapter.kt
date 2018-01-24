package fr.gstraymond.android.adapter

import android.content.Context
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.db.json.RuleList
import fr.gstraymond.utils.find
import fr.gstraymond.utils.gone
import fr.gstraymond.utils.visible

class RulesAdapter(private val context: Context,
                   private val ruleList: RuleList) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val log = Log(javaClass)

    private val contentsId = ruleList.all().indexOfFirst { it.text == "Contents" }
    private val creditsId = ruleList.all().indexOfFirst { it.text == "Credits" }
    private val glossaryId = ruleList.all().indexOfLast { it.text == "Glossary" }

    private val spacingRange = (contentsId + 1)..creditsId
    private val ruleRange = (creditsId + 1) until glossaryId

    private val urlRegex = Regex("""([\da-z.-]+)\.([a-z.]{2,6})([/\w-]*)*/?""", RegexOption.IGNORE_CASE)

    var rulesCallback: RulesCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.array_adapter_rule, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val textView = holder.itemView.find<TextView>(R.id.array_adapter_rule_text)
        val idTextView = holder.itemView.find<TextView>(R.id.array_adapter_rule_id)
        val rule = ruleList[position]

        val (style, underline) = when (rule.level) {
            1 -> android.R.style.TextAppearance_Large to true
            2 -> android.R.style.TextAppearance_Medium to false
            else -> android.R.style.TextAppearance_Small to false
        }

        val spacing =
                if (position in spacingRange) "&nbsp;".repeat(3).repeat(rule.level - 1) else ""

        val text = rule.links.fold(rule.text) { acc, ruleLink ->
            val replace = rule.text.substring(ruleLink.start..ruleLink.end)
            acc.replace(replace, """<a href="${ruleLink.id}">$replace</a>""")
        }.replace(urlRegex) { """<a href="${it.groups.firstOrNull()?.value}">${it.groups.firstOrNull()?.value}</a>""" }

        setTextViewHTML(textView, spacing + text)
        textView.setTextAppearance(context, style)
        if (underline) textView.paintFlags = textView.paintFlags or UNDERLINE_TEXT_FLAG
        else textView.paintFlags = textView.paintFlags and UNDERLINE_TEXT_FLAG.inv()

        if (position in ruleRange) {
            idTextView.visible()
            idTextView.setTextAppearance(context, style)
            idTextView.text = rule.id ?: ""
        } else {
            idTextView.gone()
        }
    }

    private fun makeLinkClickable(spannableBuilder: SpannableStringBuilder, span: URLSpan) {
        val start = spannableBuilder.getSpanStart(span)
        val end = spannableBuilder.getSpanEnd(span)
        val flags = spannableBuilder.getSpanFlags(span)
        val clickable = object : ClickableSpan() {
            override fun onClick(v: View) {
                val link = span.url
                log.d("link: $link")
                if (link.first().isDigit()) {
                    val pos = ruleList.all().indexOfFirst { it.id == link }
                    rulesCallback?.scrollTo(pos)
                } else {
                    rulesCallback?.browse(link)
                }
            }
        }
        spannableBuilder.setSpan(clickable, start, end, flags)
        spannableBuilder.removeSpan(span)
    }

    private fun setTextViewHTML(text: TextView, html: String) {
        val spanned = Html.fromHtml(html)
        val spannableBuilder = SpannableStringBuilder(spanned)
        spannableBuilder.getSpans(0, spanned.length, URLSpan::class.java)
                .forEach { makeLinkClickable(spannableBuilder, it) }
        text.text = spannableBuilder
        text.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun getItemCount() = ruleList.size()
}


interface RulesCallback {
    fun scrollTo(position: Int)
    fun browse(url: String)
}