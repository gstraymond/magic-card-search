package fr.gstraymond.biz

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import fr.gstraymond.utils.FacetParcelableUtils.readFacets
import fr.gstraymond.utils.FacetParcelableUtils.writeFacets

data class SearchOptions(var query: String = QUERY_ALL,
                         var append: Boolean = false,
                         var random: Boolean = false,
                         var addToHistory: Boolean = true,
                         var from: Int = 0,
                         var size: Int = 30,
                         var facets: Map<String, List<String>> = hashMapOf(),
                         var facetSize: Map<String, Int> = hashMapOf()) : Parcelable {

    constructor(source: Parcel) : this(
            query = source.readString(),
            append = source.readInt() == 0,
            random = source.readInt() == 0,
            from = source.readInt(),
            size = source.readInt(),
            facets = readFacets(source)
            // facetSize : pas de persistence de la taille des facettes
            // addToHistory : pas de persistence de l'ajout à l'historique
            // fromOkGoogle : pas de persistence
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(query)
        dest.writeInt(if (append) 0 else 1)
        dest.writeInt(if (random) 0 else 1)
        dest.writeInt(from)
        dest.writeInt(size)
        writeFacets(dest, facets)
        // facetSize : pas de persistence de la taille des facettes
        // addToHistory : pas de persistence de l'ajout à l'historique
        // fromOkGoogle : pas de persistence
    }

    fun updateQuery(query: String) = apply {
        this.query = when {
            TextUtils.isEmpty(query) -> QUERY_ALL
            else -> query
        }
    }

    fun updateAppend(append: Boolean) = apply {
        this.append = append
    }

    fun updateRandom(random: Boolean) = apply {
        this.random = random
    }

    fun updateFrom(from: Int) = apply {
        this.from = from
    }

    fun addFacet(facet: String, term: String) = apply {
        val terms = facets.getOrElse(facet) { listOf() } + term
        facets += facet to terms
    }

    fun removeFacet(facet: String, term: String) = apply {
        val terms = facets.getOrElse(facet) { listOf() } - term
        facets = when {
            terms.isEmpty() -> facets.filterKeys { it != facet }
            else -> facets + (facet to terms)
        }
    }

    fun addFacetSize(facet: String) = apply {
        val size = facetSize.getOrElse(facet) { 10 } + 10
        facetSize += facet to size
    }

    fun updateFacets(facets: Map<String, List<String>>) = apply {
        this.facets = facets
    }

    fun updateAddToHistory(addToHistory: Boolean) = apply {
        this.addToHistory = addToHistory
    }

    companion object {
        val QUERY_ALL = "*"

        @JvmField
        val CREATOR = object : Parcelable.Creator<SearchOptions> {
            override fun createFromParcel(source: Parcel) = SearchOptions(source)

            override fun newArray(size: Int) = arrayOfNulls<SearchOptions>(size)
        }
    }
}