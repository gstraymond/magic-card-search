package fr.gstraymond.affiliate.ebay

import java.net.URLEncoder

object LinkGenerator {

    private const val host = "https://www.ebay.com/sch/i.html"

    private val defaultParams = mapOf(
            "LH_CAds" to "",
            "_ex_kw" to "",
            "_fpos" to "",
            "_fspt" to "1",
            "_mPrRngCbx" to "1",
            "_sacat" to "38292",
            "_sadis" to "",
            "_sop" to "12",
            "_udhi" to "",
            "_udlo" to "",
            "_fosrp" to "1",
            "mkrid" to "711-53200-19255-0",
            "siteid" to "0",
            "mkcid" to "1",
            "campid" to "5338021458",
            "toolid" to "10001",
            "mkevt" to "1",
    )

    fun generate(keywords: String): String {
        val allParams = defaultParams + ("_nkw" to URLEncoder.encode(keywords, "utf-8"))

        val params = allParams
                .map { "${it.key}=${it.value}" }
                .joinToString("&")

        return "$host?$params"
    }
}