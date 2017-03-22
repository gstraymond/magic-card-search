package fr.gstraymond.db.json

import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.getId

class WishList(private val customApplication: CustomApplication,
               moshi: Moshi) :
        JsonList<Card>(
                customApplication,
                MapperUtil.fromType(moshi, Card::class.java),
                listName = "wishlist") {
    override fun Card.uid() = getId()

    override fun save(elements: List<Card>) {
        super.save(elements)
        customApplication.refreshLists()
    }

    private val mapper = MapperUtil.fromCollectionType(moshi, Card::class.java)

    fun migrate() {
        try {
            val file = "lists_wishlist"
            if (customApplication.fileList().contains(file)) {
                val json = customApplication.openFileInput(file).bufferedReader().use { it.readText() }
                save(mapper.read(json))
                customApplication.deleteFile(file)
            }
        } catch (e: Exception) {
            log.e("migrate", e)
        }
    }
}