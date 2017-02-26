package fr.gstraymond.db.json

interface UidList<A> {

    fun A.uid(): String

    fun contains(elem: A): Boolean

    fun getByUid(uid: String): A?

    operator fun get(position: Int): A

    fun size(): Int

    fun isEmpty(): Boolean

    fun all(): List<A>

    fun addOrRemove(elem: A): Boolean

    fun append(elem: A): Boolean

    fun delete(elem: A)

    fun update(elem: A)

    fun save(elements: List<A>)

    fun <B> map(f: (A) -> B): List<B>

    fun <B> flatMap(f: (A) -> List<B>): List<B>
}
