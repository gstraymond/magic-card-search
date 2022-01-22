package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.gstraymond.R
import fr.gstraymond.android.fragment.CardPagerFragment
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.find

class CardPagerActivity : CardCommonActivity(R.layout.activity_card_pager) {

    companion object {
        const val POSITION_EXTRA = "position"

        fun getIntent(context: Context, card: Card) =
                Intent(context, CardPagerActivity::class.java).apply {
                    putExtra(CARD_EXTRA, card)
                }

        fun getIntent(context: Context, card: Card, position: Int) =
                getIntent(context, card).apply {
                    putExtra(POSITION_EXTRA, position)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find(R.id.toolbar))
        true.actionBarSetDisplayHomeAsUpEnabled()

        replaceFragment(CardPagerFragment(), R.id.card_pager_container, getBundle())
    }

    override fun getBundle() = super.getBundle().apply {
        putInt(POSITION_EXTRA, intent.getIntExtra(POSITION_EXTRA, 0))
    }

    override fun replaceFragment(fragment: Fragment, id: Int) {
        if (supportFragmentManager.findFragmentById(id) == null) {
            super.replaceFragment(fragment, id)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState);
    }
}
