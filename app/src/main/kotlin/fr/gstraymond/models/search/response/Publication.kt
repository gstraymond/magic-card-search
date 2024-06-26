package fr.gstraymond.models.search.response

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
class Publication(val edition: String,
                  val editionCode: String,
                  val stdEditionCode: String?,
                  val rarity: String,
                  val rarityCode: String,
                  val image: String?,
                  val editionImage: String?,
                  val price: Double = 0.0,
                  val editionReleaseDate: Date?,
                  val foilPrice: Double = 0.0,
                  val collectorNumber: String?,
                  val mtgoPrice: Double = 0.0,
                  val mtgoFoilPrice: Double = 0.0) : Parcelable {

    constructor(source: Parcel) : this(
            edition = source.readString()!!,
            editionCode = source.readString()!!,
            stdEditionCode = source.readString(),
            rarity = source.readString()!!,
            rarityCode = source.readString()!!,
            image = source.readString(),
            editionImage = source.readString(),
            price = source.readDouble(),
            editionReleaseDate = source.readLong().run {
                if (this != 0L) Date(this) else null
            },
            foilPrice = source.readDouble(),
            collectorNumber = source.readString(),
            mtgoPrice = source.readDouble(),
            mtgoFoilPrice = source.readDouble()
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
        dest.writeDouble(foilPrice)
        dest.writeString(collectorNumber)
        dest.writeDouble(mtgoPrice)
        dest.writeDouble(mtgoFoilPrice)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Publication> {
            override fun createFromParcel(source: Parcel) = Publication(source)

            override fun newArray(size: Int) = arrayOfNulls<Publication>(size)
        }
    }
}