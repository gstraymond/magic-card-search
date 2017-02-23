package fr.gstraymond.android

import android.os.Bundle
import com.crashlytics.android.answers.ContentViewEvent
import fr.gstraymond.constants.Consts.CARD
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.getLocalizedTitle

abstract class CardCommonActivity(layoutId: Int) : CustomActivity(layoutId) {

    protected lateinit var card: Card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        card = intent.getParcelableExtra<Card>(CARD)
        title = card.getLocalizedTitle(this)
    }

    protected open fun getBundle(): Bundle = Bundle().apply { putParcelable(CARD, card) }

    override fun buildContentViewEvent(): ContentViewEvent =
            super.buildContentViewEvent().putContentId(card.title)
}
