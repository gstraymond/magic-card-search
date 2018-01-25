package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

abstract class LazyJsonList<A>(context: Context,
                               mapperUtil: MapperUtil<A>,
                               listPrefix: String = "list",
                               listName: String) : JsonList<A>(context, mapperUtil, listPrefix, listName, false) {

    interface LoadingCallback {
        fun loaded()
    }

    private val callbacks = mutableListOf<LoadingCallback>()
    private var loaded = AtomicBoolean(false)
    private val initTime = Date().time

    init {
        thread {
            init()
            setLoaded()
        }
    }

    fun registerLoading(callback: LoadingCallback) = callbacks.add(callback)

    fun isLoaded() = loaded.get()

    fun setLoaded() {
        if (loaded.compareAndSet(false, true)) {
            log.d("loadComplete: $listName is loaded - ${elems.size} elems in ${Date().time - initTime}ms")
            callbacks.forEach { it.loaded() }
            callbacks.clear()
        }
    }
}