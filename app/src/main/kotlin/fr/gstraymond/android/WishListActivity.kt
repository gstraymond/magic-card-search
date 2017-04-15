package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import fr.gstraymond.R
import fr.gstraymond.android.adapter.WishlistAdapter
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.*

class WishListActivity : CustomActivity(R.layout.activity_wishlist) {

    companion object {
        fun getIntent(context: Context) =
                Intent(context, WishListActivity::class.java)
    }

    private val emptyTextViewId = R.id.wishlist_empty_text
    private val clickCallbacks = object : WishlistAdapter.ClickCallbacks {
        override fun onEmptyList() = show(emptyTextViewId)

        override fun cardClicked(card: Card) = startActivity {
            CardDetailActivity.getIntent(this@WishListActivity, card)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.wishlist_title)


        find<RecyclerView>(R.id.wishlist_recyclerview).let {
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = WishlistAdapter(this, app().wishList, clickCallbacks)
        }
    }

    override fun onResume() {
        super.onResume()
        if (app().wishList.isEmpty()) {
            show(emptyTextViewId)
        } else {
            hide(emptyTextViewId)
        }
    }
}