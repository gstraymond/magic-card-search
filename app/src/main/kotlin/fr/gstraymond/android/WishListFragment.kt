package fr.gstraymond.android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.adapter.WishlistAdapter
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.*

class WishListFragment : Fragment(), WishlistAdapter.ClickCallbacks {

    private val wishList by lazy { app().wishList }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_lists, container, false).apply {
                find<RecyclerView>(R.id.lists_recyclerview).let {
                    it.layoutManager = LinearLayoutManager(context)
                    it.adapter = WishlistAdapter(context, wishList, this@WishListFragment)
                }

                find<TextView>(R.id.lists_empty_text).setText(R.string.wishlist_empty_text)
            }

    override fun onResume() {
        super.onResume()
        if (wishList.isEmpty()) {
            view?.show(R.id.lists_empty_text)
        } else {
            view?.hide(R.id.lists_empty_text)
        }
    }

    override fun onEmptyList() {
        view?.show(R.id.lists_empty_text)
    }

    override fun cardClicked(card: Card) = startActivity {
        CardDetailActivity.getIntent(context, card)
    }
}