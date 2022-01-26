package fr.gstraymond.android.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.gstraymond.R;
import fr.gstraymond.android.CardCommonActivity;
import fr.gstraymond.android.CardPagerActivity;
import fr.gstraymond.models.search.response.Card;
import fr.gstraymond.ui.CardViewPager;
import fr.gstraymond.ui.adapter.CardPagerAdapter;

public class CardPagerFragment extends Fragment {

    private int position = 0;

    private String CARD = CardCommonActivity.CARD_EXTRA;
    private String POSITION = CardPagerActivity.POSITION_EXTRA;

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
