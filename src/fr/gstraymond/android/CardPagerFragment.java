package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.CARD;
import static fr.gstraymond.constants.Consts.POSITION;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.gstraymond.R;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.ui.CardPagerAdapter;
import fr.gstraymond.ui.CardViewPager;

public class CardPagerFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Card card = getArguments().getParcelable(CARD);
		int position = 0;
		if (getArguments().containsKey(POSITION)) {
			position = getArguments().getInt(POSITION);
		}

		View rootView = inflater.inflate(R.layout.fragment_card_pager,
				container, false);

		CardViewPager viewPager = getViewPager(card, rootView);
		viewPager.setAdapter(getPagerAdapter(card));
		viewPager.setCurrentItem(position);

		return rootView;
	}

	private CardViewPager getViewPager(Card card, View rootView) {
		CardViewPager viewPager = ((CardViewPager) rootView
				.findViewById(R.id.pager)).setCard(card);
		return viewPager;
	}

	private PagerAdapter getPagerAdapter(Card card) {
		return new CardPagerAdapter(getFragmentManager()).setCard(card);
	}
}
