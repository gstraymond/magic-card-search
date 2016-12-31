package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.magic.card.search.commons.log.Log
import fr.gstraymond.android.CustomApplication
import java.io.FileNotFoundException

abstract class JsonList<A>(private val customApplication: CustomApplication,
                           private val mapperUtil: MapperUtil<List<A>>,
                           listName: String) {

    private val log = Log(this)
    private val listName = "lists_" + listName

    val elems: MutableList<A> = load()
    private var index: Map<String, A> = loadIndex()

    abstract fun getId(elem: A): String

    private fun loadIndex() = elems.map { getId(it) to it }.toMap()

    private fun load(): MutableList<A> {
        try {
            val inputStream = customApplication.openFileInput(listName)
            val result = mapperUtil.read(inputStream)
            return when (result) {
                null -> {
                    save(listOf())
                    mutableListOf()
                }
                else -> result.toMutableList()
            }
        } catch (e: FileNotFoundException) {
            log.w("get: %s", e)
            save(listOf())
            return mutableListOf()
        }
    }

    private fun save(elems: List<A>) {
        try {
            customApplication.openFileOutput(listName, Context.MODE_PRIVATE).apply {
                write(mapperUtil.asJsonString(elems).toByteArray())
                close()
            }
            customApplication.refreshLists()
        } catch (e: Exception) {
            log.e("save", e)
        }
    }

    fun addOrRemove(elem: A): Boolean {
        val contain = contains(elem)
        if (!contain) {
            elems.add(elem)
            index += (getId(elem) to elem)
        } else {
            elems.remove(elem)
            index = index.filterKeys { it != getId(elem) }
        }
        save(elems)
        log.d("addOrRemove %s -> removed? %s", elem, contain)
        return !contain
    }

    fun contains(elem: A): Boolean = index.contains(getId(elem))

    fun get(id: String): A? = index[id]
}
