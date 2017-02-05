package fr.gstraymond.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.gstraymond.R;
import fr.gstraymond.models.search.response.Card;
import fr.gstraymond.ui.CardViewPager;
import fr.gstraymond.ui.adapter.CardPagerAdapter;

import static fr.gstraymond.constants.Consts.CARD;
import static fr.gstraymond.constants.Consts.POSITION;

public class CardPagerFragment extends Fragment {

    private int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Card card = getArguments().getParcelable(CARD);

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION)) {
            position = savedInstanceState.getInt(POSITION);
        } else if (getArguments().containsKey(POSITION)) {
            position = getArguments().getInt(POSITION);
        }

        View rootView = inflater.inflate(R.layout.fragment_card_pager, container, false);

        CardViewPager viewPager = getViewPager(card, rootView);
        viewPager.setAdapter(getPagerAdapter(card));
        viewPager.setCurrentItem(position);

        return rootView;
    }

    private CardViewPager getViewPager(Card card, View rootView) {
        return ((CardViewPager) rootView.findViewById(R.id.pager)).setCard(card);
    }

    private PagerAdapter getPagerAdapter(Card card) {
        return new CardPagerAdapter(getFragmentManager()).setCard(card);
    }


    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(POSITION, position);
        super.onSaveInstanceState(outState);
    }
}
