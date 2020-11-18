package fr.gstraymond.android

import android.os.Bundle
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.getLocalizedTitle

abstract class CardCommonActivity(layoutId: Int) : CustomActivity(layoutId) {

    companion object {
        const val CARD_EXTRA = "card"
    }

    protected lateinit var card: Card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        card = intent.getParcelableExtra(CARD_EXTRA)!!
        title = card.getLocalizedTitle(this)
    }

    protected open fun getBundle(): Bundle = Bundle().apply { putParcelable(CARD_EXTRA, card) }
}
