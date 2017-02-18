package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import fr.gstraymond.android.CustomApplication
import java.io.FileNotFoundException

abstract class JsonList<A>(private val customApplication: CustomApplication,
                           private val mapperUtil: MapperUtil<List<A>>,
                           listName: String) : MemoryUidList<A>() {

    private val listName = "lists_" + listName

    override val elems = load()
    override val index = loadIndex()

    private fun loadIndex() = mutableMapOf(*(elems.map { it.uid() to it }.toTypedArray()))

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

    fun save(elems: List<A>) {
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

    override fun addOrRemove(elem: A) = super.addOrRemove(elem).apply {
        save(elems)
    }
}
