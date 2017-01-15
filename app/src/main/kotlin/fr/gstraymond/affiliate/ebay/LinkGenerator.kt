package fr.gstraymond.affiliate.ebay

import java.net.URLEncoder

object LinkGenerator {

    private val host = "http://rover.ebay.com/rover/1/711-53200-19255-0/1"

    private val defaultParams = mapOf(
            "icep_ff3" to "9",
            "pub" to "5575261879",
            "toolid" to "10001",
            "campid" to "5338021458",
            "customid" to "",
            "icep_sellerId" to "",
            "icep_ex_kw" to "",
            "icep_sortBy" to "12",
            "icep_catId" to "38292",
            "icep_minPrice" to "",
            "icep_maxPrice" to "",
            "ipn" to "psmain",
            "icep_vectorid" to "229466",
            "kwid" to "902099",
            "mtid" to "824",
            "kw" to "lg"
    )

    fun generate(keywords: String): String {
        val allParams = defaultParams + ("icep_uq" to URLEncoder.encode(keywords, "utf-8"))

        val params = allParams
                .map { "${it.key}=${it.value}" }
                .joinToString("&")

        return "$host?$params"
    }
}