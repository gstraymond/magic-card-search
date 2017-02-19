package fr.gstraymond.android

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import fr.gstraymond.R
import fr.gstraymond.android.adapter.WishlistAdapter
import fr.gstraymond.constants.Consts.CARD
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.find
import fr.gstraymond.utils.hide
import fr.gstraymond.utils.show

class WishListActivity : CustomActivity(R.layout.activity_wishlist),
        WishlistAdapter.ClickCallbacks {

    private val wishlist by lazy { customApplication.wishList }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.wishlist_title)
        }

        find<RecyclerView>(R.id.wishlist_recyclerview).let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = WishlistAdapter(this, wishlist, this)
        }
    }

    override fun onEmptyList() {
        show(R.id.wishlist_empty_text)
    }

    override fun onResume() {
        super.onResume()
        if (wishlist.isEmpty()) {
            show(R.id.wishlist_empty_text)
        } else {
            hide(R.id.wishlist_empty_text)
        }
    }

    override fun cardClicked(card: Card) {
        startActivity {
            Intent(this, CardDetailActivity::class.java).apply {
                putExtra(CARD, card)
            }
        }
    }

    override fun buildContentViewEvent() =
            super.buildContentViewEvent()
                    .putCustomAttribute("wishlist_size", wishlist.size())

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.wishlist_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.wishlist_decklist -> {
                startActivity(DeckListActivity.getIntent(this))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
