package fr.gstraymond.models

import android.os.Parcel
import android.os.Parcelable
import fr.gstraymond.utils.FacetParcelableUtils.readFacets
import fr.gstraymond.utils.FacetParcelableUtils.writeFacets
import java.util.*

data class History(val query: String,
                   var isFavorite: Boolean,
                   val facets: Map<String, List<String>>,
                   val date: Date) : Parcelable {

    constructor(source: Parcel) : this(
            query = source.readString()!!,
            date = Date(source.readLong()),
            isFavorite = source.readString() == "1",
            facets = readFacets(source)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(query)
        dest.writeLong(date.time)
        dest.writeString(if (isFavorite) "1" else "0")
        writeFacets(dest, facets)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<History> {
            override fun createFromParcel(source: Parcel) = History(source)

            override fun newArray(size: Int) = arrayOfNulls<History>(size)
        }
    }
}
