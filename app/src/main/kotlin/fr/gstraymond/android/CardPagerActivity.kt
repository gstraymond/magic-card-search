package fr.gstraymond.android

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.Toolbar

import fr.gstraymond.R
import fr.gstraymond.android.fragment.CardPagerFragment

import fr.gstraymond.constants.Consts.POSITION
import fr.gstraymond.utils.find

class CardPagerActivity : CardCommonActivity(R.layout.activity_card_pager) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        actionBarSetDisplayHomeAsUpEnabled(true)

        replaceFragment(CardPagerFragment(), R.id.card_pager_container, getBundle())
    }

    override fun getBundle() = super.getBundle().apply {
        putInt(POSITION, intent.getIntExtra(POSITION, 0))
    }

    override fun replaceFragment(fragment: Fragment, id: Int) {
        if (fragmentManager.findFragmentById(id) == null) {
            super.replaceFragment(fragment, id)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState);
    }
}
