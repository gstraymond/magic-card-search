package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.magic.card.search.commons.log.Log
import java.io.FileNotFoundException

abstract class JsonList<A>(private val context: Context,
                           private val mapperUtil: MapperUtil<List<A>>,
                           listName: String) {

    private val log = Log(this)
    private val listName = "lists_" + listName

    val elems: MutableList<A>  = load()
    private val index: MutableSet<String>  = loadIndex()

    abstract fun getId(elem: A): String

    private fun loadIndex() = elems.map { getId(it) }.toHashSet()

    private fun load(): MutableList<A> {
        try {
            val inputStream = context.openFileInput(listName)
            return mapperUtil.read(inputStream).toMutableList()
        } catch (e: FileNotFoundException) {
            log.w("get: %s", e)
            save()
            return mutableListOf()
        }
    }

    private fun save() {
        try {
            context.openFileOutput(listName, Context.MODE_PRIVATE).apply {
                write(mapperUtil.asJsonString(elems).toByteArray())
                close()
            }
        } catch (e: Exception) {
            log.e("save", e)
        }
    }

    fun addOrRemove(elem: A): Boolean {
        val contain = contains(elem)
        if (!contain) {
            elems.add(elem)
            index.add(getId(elem))
        } else {
            elems.remove(elem)
            index.remove(getId(elem))
        }
        save()
        log.d("addOrRemove %s -> removed? %s", elem, contain)
        return !contain
    }

    fun contains(elem: A): Boolean {
        return index.contains(getId(elem))
    }

    fun get(id: String): A? {
        return elems.find { getId(it) == id }
    }
}
