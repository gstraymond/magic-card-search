package fr.gstraymond.db.json

import android.content.Context
import android.content.Context.MODE_APPEND
import android.content.Context.MODE_PRIVATE
import com.magic.card.search.commons.json.MapperUtil
import java.io.FileNotFoundException
import java.io.FileOutputStream

abstract class JsonList<A>(private val context: Context,
                           private val mapperUtil: MapperUtil<A>,
                           listPrefix: String = "list",
                           listName: String) : MemoryUidList<A>() {

    private val listName = "${listPrefix}_$listName"

    override val elems = load()
    override val index = loadIndex()

    private fun loadIndex() =
            mutableMapOf(*(elems.map { it.uid() to it }.toTypedArray()))

    private fun load(): MutableList<A> {
        try {
            return mutableListOf<A>().apply {
                context.openFileInput(listName).bufferedReader().useLines {
                    it.forEach {
                        mapperUtil.read(it)?.let { add(it) }
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            log.w("get: %s", e)
            //save(listOf())
            return mutableListOf()
        }
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
        write(MODE_APPEND) { it.write(elem) }
    }

    private fun write(mode: Int, f: (FileOutputStream) -> Unit) {
        try {
            context.openFileOutput(listName, mode).use { f(it) }
        } catch (e: Exception) {
            log.e("save", e)
        }
    }

    private fun writeAll() {
        write(MODE_PRIVATE) { stream ->
            elems.map { stream.write(it) }
        }
    }

    private fun FileOutputStream.write(elem: A) {
        log.d("write $elem")
        write("${mapperUtil.asJsonString(elem)}\n".toByteArray())
    }
}
