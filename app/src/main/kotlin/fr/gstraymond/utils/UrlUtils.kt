package fr.gstraymond.utils

import java.net.URL

fun URL.getParameters(): Map<String, String> = query?.split("&")?.map {
    it.split(Regex("="), 2).run { get(0) to get(1) }
}?.toMap() ?: mapOf()

fun URL.getPathSegment(): List<String> = path.split("/")