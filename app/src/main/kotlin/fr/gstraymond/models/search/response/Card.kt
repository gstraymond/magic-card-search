package fr.gstraymond.models.search.response

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import fr.gstraymond.tools.LanguageUtil
import java.util.*

class Card(val title: String,
           val frenchTitle: String?,
           val type: String,
           val castingCost: String?,
           val power: String?,
           val toughness: String?,
           val description: String,
           val publications: List<Publication>,
           val formats: List<String>,
           val colors: List<String>,
           val layout: String,
           val loyalty: String?,
           val altTitles: List<String>,
           val convertedManaCost: Int,
           val abilities: List<String>,
           val ruling: List<Ruling>,
           val land: List<String>) : Parcelable {

    constructor(source: Parcel) : this(
            title = source.readString()!!,
            frenchTitle = source.readString(),
            type = source.readString()!!,
            castingCost = source.readString(),
            power = source.readString(),
            toughness = source.readString(),
            description = source.readString()!!,
            publications = ArrayList<Publication>().apply {
                source.readList(this as List<*>, Publication::class.java.classLoader)
            },
            formats = ArrayList<String>().apply {
                source.readList(this as List<*>, String::class.java.classLoader)
            },
            colors = ArrayList<String>().apply {
                source.readList(this as List<*>, String::class.java.classLoader)
            },
            layout = source.readString()!!,
            loyalty = source.readString(),
            altTitles = ArrayList<String>().apply {
                source.readList(this as List<*>, String::class.java.classLoader)
            },
            convertedManaCost = source.readInt() ?: 0,
            abilities = ArrayList<String>().apply {
                source.readList(this as List<*>, String::class.java.classLoader)
            },
            ruling = ArrayList<Ruling>().apply {
                source.readList(this as List<*>, Ruling::class.java.classLoader)
            },
            land = ArrayList<String>().apply {
                source.readList(this as List<*>, String::class.java.classLoader)
            }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(frenchTitle)
        dest.writeString(type)
        dest.writeString(castingCost)
        dest.writeString(power)
        dest.writeString(toughness)
        dest.writeString(description)
        dest.writeList(publications)
        dest.writeList(formats)
        dest.writeList(colors)
        dest.writeString(layout)
        dest.writeString(loyalty)
        dest.writeList(altTitles)
        dest.writeInt(convertedManaCost ?: 0)
        dest.writeList(abilities)
        dest.writeList(ruling)
        dest.writeList(land)
    }

    override fun toString() = title

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Card> {
            override fun createFromParcel(source: Parcel) = Card(source)

            override fun newArray(size: Int) = arrayOfNulls<Card>(size)
        }
    }
}

fun Card.getLocalizedTitle(context: Context): String =
        getLocalizedTitle(context, Card::title, { _, ft -> ft })

fun Card.getLocalizedTitle(context: Context,
                           english: (Card) -> String,
                           french: (Card, String) -> String): String = when {
    LanguageUtil.showFrench(context) && frenchTitle != null -> french(this, this.frenchTitle)
    else -> english(this)
}