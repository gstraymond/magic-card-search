package fr.gstraymond.db.json

import android.content.Context
import android.content.Context.MODE_APPEND
import android.content.Context.MODE_PRIVATE
import com.magic.card.search.commons.json.MapperUtil
import java.io.FileNotFoundException
import java.io.FileOutputStream
import kotlin.concurrent.thread

abstract class JsonList<A>(private val context: Context,
                           private val mapperUtil: MapperUtil<A>,
                           listPrefix: String = "list",
                           listName: String,
                           loadOnInit: Boolean = true) : MemoryUidList<A>() {

    protected val listName = "${listPrefix}_$listName"

    override val elems: MutableList<A> = mutableListOf()
    override val index: MutableMap<String, A> = mutableMapOf()

    init {
        if (loadOnInit) init()
    }

    protected fun init() {
        elems.addAll(
                try {
                    mutableListOf<A>().apply {
                        context.openFileInput(listName).bufferedReader().useLines {
                            it.forEach {
                                mapperUtil.read(it)?.let { add(it) }
                            }
                        }
                    }
                } catch (e: FileNotFoundException) {
                    log.w("get: %s", e)
                    //save(listOf())
                    mutableListOf<A>()
                })

        index.putAll(
                mutableMapOf(*(elems.map { it.uid() to it }.toTypedArray()))
        )
    }

    override fun save(elements: List<A>) {
        super.save(elements)
        writeAll()
    }

    override fun delete(elem: A) = super.delete(elem).apply { writeAll() }

    override fun update(elem: A) = super.update(elem).apply { writeAll() }

    override fun clear() = super.clear().apply {
        try {
            context.deleteFile(listName)
        } catch (e: Exception) {
            log.e("clear: $listName", e)
        }
    }

    override fun append(elem: A) = super.append(elem).apply {
        synchronized(this) {
            write(MODE_APPEND) { it.write(elem) }
        }
    }

    private fun write(mode: Int, f: (FileOutputStream) -> Unit) {
        try {
            context.openFileOutput(listName, mode).use { f(it) }
        } catch (e: Exception) {
            log.e("save", e)
        }
    }

    private fun writeAll() {
        thread {
            synchronized(this) {
                log.d("write $javaClass")
                write(MODE_PRIVATE) { stream ->
                    elems.map { stream.write(it) }
                }
            }
        }
    }

    private fun FileOutputStream.write(elem: A) {
        write("${mapperUtil.asJsonString(elem)}\n".toByteArray())
    }
}
