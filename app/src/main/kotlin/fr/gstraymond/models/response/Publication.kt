package fr.gstraymond.models.response

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Publication (val edition: String,
                   val editionCode: String,
                   val stdEditionCode: String?,
                   val rarity: String,
                   val rarityCode: String,
                   val image: String?,
                   val editionImage: String?,
                   val price: Double = 0.toDouble(),
                   val editionReleaseDate: Date?) : Parcelable {

    constructor(source: Parcel) : this(
        edition = source.readString(),
        editionCode = source.readString(),
        stdEditionCode = source.readString(),
        rarity = source.readString(),
        rarityCode = source.readString(),
        image = source.readString(),
        editionImage = source.readString(),
        price = source.readDouble(),
        editionReleaseDate = source.readLong().run {
            if (this != 0L) Date(this) else null
        }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(edition)
        dest.writeString(editionCode)
        dest.writeString(stdEditionCode)
        dest.writeString(rarity)
        dest.writeString(rarityCode)
        dest.writeString(image)
        dest.writeString(editionImage)
        dest.writeDouble(price)
        dest.writeLong(editionReleaseDate?.time ?: 0)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Publication> {
            override fun createFromParcel(source: Parcel) = Publication(source)

            override fun newArray(size: Int) = arrayOfNulls<Publication>(size)
        }
    }
}