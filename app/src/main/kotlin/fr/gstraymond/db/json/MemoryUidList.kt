package fr.gstraymond.db.json

import com.magic.card.search.commons.log.Log
import java.util.*

abstract class MemoryUidList<A> : UidList<A> {

    protected val log = Log(javaClass)

    protected abstract val elems: MutableList<A>
    protected abstract val index: MutableMap<String, A>

    override fun contains(elem: A) = index.contains(elem.uid())

    override fun getByUid(uid: String): A? = index[uid]

    override operator fun get(position: Int): A = elems[position]

    override fun size(): Int = elems.size

    override fun isEmpty(): Boolean = elems.isEmpty()

    override fun all(): List<A> = elems

    override fun addOrRemove(elem: A): Boolean {
        val append = append(elem)
        if (!append) {
            delete(elem)
        }
        log.d("addOrRemove $elem -> added? $append")
        return append
    }

    override fun append(elem: A): Boolean {
        val appended = !contains(elem)
        if (appended) {
            elems.add(elem)
            index[elem.uid()] = elem
        }
        log.d("append $elem appended? $appended")
        return appended
    }

    override fun delete(elem: A) {
        val id = elem.uid()
        elems.remove(getByUid(id))
        index.remove(id)
        log.d("delete $elem")
    }

    override fun update(elem: A) {
        val contains = contains(elem)
        log.d("update $elem ? $contains")
        if (contains) {
            val uid = elem.uid()
            elems.remove(getByUid(uid))
            elems.add(elem)
            index[uid] = elem
        }
    }

    override fun save(elements: List<A>) {
        val clone = ArrayList(elements) // be sure to have another instance
        log.d("save $javaClass / ${clone.size}")
        elems.clear()
        elems.addAll(clone)
        index.clear()
        clone.forEach { // elems could be accessed from another thread
            index[it.uid()] = it
        }
    }

    override fun clear() {
        log.d("clear")
        elems.clear()
        index.clear()
    }

    override fun <B> map(f: (A) -> B): List<B> = elems.map(f)

    override fun <B> flatMap(f: (A) -> List<B>): List<B> = elems.flatMap(f)

    override fun filter(predicate: (A) -> Boolean) = elems.filter(predicate)
}