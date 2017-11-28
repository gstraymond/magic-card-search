package fr.gstraymond.tools

import fr.gstraymond.R

object CardColorUtil {

    fun getColorId(colors: List<String>,
                   type: String,
                   land: List<String>): Int {
        return if (colors.contains("Gold"))
            R.color.gold
        else if (colors.contains("White"))
            R.color.white
        else if (colors.contains("Red"))
            R.color.red
        else if (colors.contains("Green"))
            R.color.green
        else if (colors.contains("Black"))
            R.color.black
        else if (colors.contains("Blue"))
            R.color.blue
        else if (colors.contains("Uncolored") && type.contains("Artifact"))
            R.color.uncolored
        else if (type.contains("Land")) {
            val produce = land.filter { it.startsWith("Produce") }.map { it.split(" ")[1] }
            when {
                produce.size > 1 -> R.color.gold
                produce.isEmpty() -> android.R.color.white
                produce.first() == "Red" -> R.color.red
                produce.first() == "Green" -> R.color.green
                produce.first() == "Black" -> R.color.black
                produce.first() == "Blue" -> R.color.blue
                produce.first() == "Uncolored" -> R.color.uncolored
                else -> android.R.color.white
            }
        }
        else android.R.color.white
    }
}
