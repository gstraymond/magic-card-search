package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.affiliate.ebay.LinkGenerator
import fr.gstraymond.android.fragment.CardDetailFragment
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.ui.adapter.CardDetailViews
import fr.gstraymond.ui.view.impl.ShareView
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.startActivity

class CardDetailActivity : CardCommonActivity(R.layout.activity_card_detail),
        CardDetailFragment.Callbacks {

    companion object {
        fun getIntent(context: Context, card: Card) =
                Intent(context, CardDetailActivity::class.java).apply {
                    putExtra(CARD_EXTRA, card)
                }
    }

    private val rootView by lazy { find<View>(android.R.id.content) }
    private val picsView by lazy { find<TextView>(R.id.card_detail_pics) }
    private val ebayView by lazy { find<TextView>(R.id.card_detail_ebay) }
    private val deckView by lazy { find<TextView>(R.id.card_detail_deck) }
    private val favoriteView by lazy { CardDetailViews(app(), this, ShareViewCallbacks()) }

    private inner class ShareViewCallbacks : ShareView.ShareViewCallbacks {

        override fun wishlistChanged(position: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find(R.id.toolbar))
        true.actionBarSetDisplayHomeAsUpEnabled()

        val title = find<TextView>(R.id.toolbar_title)
        title.text = card.getLocalizedTitle(this, Card::title) { c, ft -> "$ft (${c.title})" }

        replaceFragment(CardDetailFragment(), R.id.card_detail_container, getBundle())

        picsView.setOnClickListener {
            startActivity { CardPagerActivity.getIntent(this, card) }
        }
        
        ebayView.setOnClickListener {
            startActivity {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(LinkGenerator.generate(card.title))
                }
            }
        }

        deckView.setOnClickListener {
            startActivity { CardDeckActivity.getIntent(this, card) }
        }
    }

    override fun onResume() {
        super.onResume()
        favoriteView.display(rootView, card, 0)
    }

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
