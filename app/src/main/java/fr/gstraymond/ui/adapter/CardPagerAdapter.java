package fr.gstraymond.ui.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.search.model.response.Publication;
import fr.gstraymond.ui.CardFragment;

public class CardPagerAdapter extends FragmentStatePagerAdapter {

    private Card card;

    public CardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Publication publication = card.getPublications().get(position);

        Bundle bundle = new Bundle();
        bundle.putString(CardFragment.URL, publication.getImage());

        CardFragment cardFragment = new CardFragment();
        cardFragment.setArguments(bundle);
        return cardFragment;
    }

    @Override
    public int getCount() {
        return card.getPublications().size();
    }

    public CardPagerAdapter setCard(Card card) {
        this.card = card;
        return this;
    }
}
