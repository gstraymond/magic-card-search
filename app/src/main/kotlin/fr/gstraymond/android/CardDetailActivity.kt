package fr.gstraymond.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import fr.gstraymond.R
import fr.gstraymond.affiliate.ebay.LinkGenerator
import fr.gstraymond.android.fragment.CardDetailFragment
import fr.gstraymond.constants.Consts.CARD
import fr.gstraymond.constants.Consts.POSITION
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.getLocalizedTitle

class CardDetailActivity : CardCommonActivity(R.layout.activity_card_detail),
        CardDetailFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        actionBarSetDisplayHomeAsUpEnabled(true)

        val title = findViewById(R.id.toolbar_title) as TextView
        title.text = card.getLocalizedTitle(this, Card::title, { c, ft -> "$ft (${c.title})" })

        replaceFragment(CardDetailFragment(), R.id.card_detail_container, getBundle())
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.pictures_tab -> {
            startActivity {
                Intent(this, CardPagerActivity::class.java).apply {
                    putExtra(CARD, card)
                }
            }
            true
        }

        R.id.ebay_tab -> {
            startActivity {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(LinkGenerator.generate(card.title))
                }
            }
            val event = CustomEvent("ebay").putCustomAttribute("card", card.title)
            Answers.getInstance().logCustom(event)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu) =
            menuInflater.inflate(R.menu.card_detail_menu, menu).run { true }

    override fun onItemSelected(id: Int) {
        startActivity {
            Intent(this, CardPagerActivity::class.java).apply {
                putExtra(CARD, card)
                putExtra(POSITION, id)
            }
        }
    }

    override fun onListSelected(list: String) {
        startActivity {
            when (list) {
                "wishlist" -> Intent(this, WishListActivity::class.java)
                else -> DeckDetailActivity.getIntent(this, list)
            }
        }
    }
}
