package com.magic.card.search.browser;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.magic.card.search.browser.adapter.CardsAdapter;
import com.magic.card.search.commons.model.MTGCard;

import java.util.List;

public class MainDataPresenter {

    private Activity activity;
    private CardsAdapter.CardClickListener cardClickListener;

    private CardsAdapter adapter;

    public MainDataPresenter(Activity activity, CardsAdapter.CardClickListener cardClickListener) {
        this.activity = activity;
        this.cardClickListener = cardClickListener;
    }

    public void bind() {
        RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.content_main_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new CardsAdapter(cardClickListener);
        recyclerView.setAdapter(adapter);
    }

    public void setCards(List<MTGCard> cards) {
        adapter.setCards(cards);
    }
}
