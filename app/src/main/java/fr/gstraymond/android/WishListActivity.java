package fr.gstraymond.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.crashlytics.android.answers.ContentViewEvent;

import fr.gstraymond.R;
import fr.gstraymond.android.adapter.WishlistAdapter;
import fr.gstraymond.db.json.JsonList;
import fr.gstraymond.models.response.Card;

import static fr.gstraymond.constants.Consts.CARD;

public class WishListActivity extends CustomActivity implements WishlistAdapter.ClickCallbacks {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.wishlist_title));

        WishlistAdapter adapter = new WishlistAdapter(
                this,
                getCustomApplication().getWishlist(),
                this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.wishlist_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (!((JsonList) getCustomApplication().getWishlist()).getElems().isEmpty()) {
            findViewById(R.id.wishlist_empty_text).setVisibility(View.GONE);
        }
    }

    @Override
    public void onEmptyList() {
        findViewById(R.id.wishlist_empty_text).setVisibility(View.VISIBLE);
    }

    @Override
    public void cardClicked(Card card) {
        Intent intent = new Intent(this, CardDetailActivity.class);
        intent.putExtra(CARD, card);
        startActivity(intent);
    }

    @Override
    protected ContentViewEvent buildContentViewEvent() {
        ContentViewEvent event = super.buildContentViewEvent();
        event.putCustomAttribute("wishlist_size", getCustomApplication().getWishlist().getElems().size());
        return event;
    }
}
