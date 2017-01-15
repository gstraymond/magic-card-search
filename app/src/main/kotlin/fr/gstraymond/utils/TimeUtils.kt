package fr.gstraymond.utils

fun <A> time(f: () -> A): Pair<A, Long> {
    val httpNow = System.currentTimeMillis()
    val result = f()
    val httpDuration = System.currentTimeMillis() - httpNow
    return result to httpDuration
}