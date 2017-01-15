package fr.gstraymond.android

import android.os.Bundle

import com.crashlytics.android.answers.ContentViewEvent

import fr.gstraymond.models.search.response.Card
import fr.gstraymond.tools.LanguageUtil

import fr.gstraymond.constants.Consts.CARD

abstract class CardCommonActivity : CustomActivity() {

    protected lateinit var card: Card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        card = intent.getParcelableExtra<Card>(CARD)
        title = when {
            LanguageUtil.showFrench(this) && card.frenchTitle != null -> card.frenchTitle
            else -> card.title
        }
    }

    protected open fun getBundle(): Bundle = Bundle().apply { putParcelable(CARD, card) }

    override fun buildContentViewEvent(): ContentViewEvent =
            super.buildContentViewEvent().putContentId(card.title)
}
