package fr.gstraymond.android

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import fr.gstraymond.R
import fr.gstraymond.android.adapter.WishlistAdapter
import fr.gstraymond.constants.Consts.CARD
import fr.gstraymond.db.json.WishList
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.find
import fr.gstraymond.utils.hide
import fr.gstraymond.utils.show

class WishListFragment(private val wishList: WishList) : Fragment(),
        WishlistAdapter.ClickCallbacks {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_lists, container, false).apply {
                find<RecyclerView>(R.id.lists_recyclerview).let {
                    it.layoutManager = LinearLayoutManager(context)
                    it.adapter = WishlistAdapter(context, wishList, this@WishListFragment)
                }
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

    override fun cardClicked(card: Card) {
        val intent = Intent(context, CardDetailActivity::class.java).apply {
            putExtra(CARD, card)
        }
        context.startActivity(intent)
    }
}