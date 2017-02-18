package fr.gstraymond.db.json

import com.magic.card.search.commons.log.Log

interface UidList<A> {

    fun A.uid(): String

    fun contains(elem: A): Boolean

    fun getByUid(uid: String): A?

    operator fun get(position: Int): A

    fun size(): Int

    fun all(): List<A>

    fun addOrRemove(elem: A): Boolean
}

abstract class MemoryUidList<A> : UidList<A> {

    protected val log = Log(javaClass)

    abstract protected val elems: MutableList<A>
    abstract protected val index: MutableMap<String, A>

    override fun contains(elem: A) = index.contains(elem.uid())

    override fun getByUid(uid: String): A? = index[uid]

    override operator fun get(position: Int): A = elems[position]

    override fun size(): Int = elems.size

    override fun all(): List<A> = elems

    override fun addOrRemove(elem: A): Boolean {
        val contain = contains(elem)
        val id = elem.uid()
        if (!contain) {
            elems.add(elem)
            index.put(id, elem)
        } else {
            elems.remove(getByUid(id))
            index.remove(id)
        }
        log.d("addOrRemove $elem -> removed? $contain")
        return !contain
    }
}