package fr.gstraymond.models.search.response

import android.os.Parcel
import android.os.Parcelable

class Ruling(val date: String,
             val text: String) : Parcelable {

    constructor(source: Parcel) : this(
            date = source.readString(),
            text = source.readString())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(date)
        dest.writeString(text)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Ruling> {
            override fun createFromParcel(source: Parcel) = Ruling(source)

            override fun newArray(size: Int) = arrayOfNulls<Ruling>(size)
        }
    }
}