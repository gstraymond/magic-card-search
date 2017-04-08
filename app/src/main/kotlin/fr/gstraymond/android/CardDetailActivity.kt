package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.affiliate.ebay.LinkGenerator
import fr.gstraymond.analytics.Tracker
import fr.gstraymond.android.fragment.CardDetailFragment
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.utils.startActivity

class CardDetailActivity : CardCommonActivity(R.layout.activity_card_detail),
        CardDetailFragment.Callbacks {

    companion object {
        fun getIntent(context: Context, card: Card) =
                Intent(context, CardDetailActivity::class.java).apply {
                    putExtra(CARD_EXTRA, card)
                }
    }

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
        R.id.pictures_tab -> startActivity {
            CardPagerActivity.getIntent(this, card)
        }.run {
            true
        }

        R.id.ebay_tab -> {
            startActivity {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(LinkGenerator.generate(card.title))
                }
            }
            Tracker.ebayCart(card)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu) =
            menuInflater.inflate(R.menu.card_detail_menu, menu).run { true }

    override fun onItemSelected(id: Int) = startActivity {
        CardPagerActivity.getIntent(this, card, id)
    }

    override fun onListSelected(list: String) {
        startActivity {
            when (list) {
                "wishlist" -> WishListActivity.getIntent(this)
                else -> DeckDetailActivity.getIntent(this, list)
            }
        }
    }
}
