package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.MAGIC_CARD;
import static fr.gstraymond.constants.Consts.POSITION;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.gstraymond.R;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.ui.MagicCardPagerAdapter;
import fr.gstraymond.ui.MagicCardViewPager;

public class MagicCardPagerFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MagicCard card = getArguments().getParcelable(MAGIC_CARD);
		int position = 0;
		if (getArguments().containsKey(POSITION)) {
			position = getArguments().getInt(POSITION);
		}

		View rootView = inflater.inflate(R.layout.fragment_magiccard_pager,
				container, false);

		MagicCardViewPager viewPager = getViewPager(card, rootView);
		viewPager.setAdapter(getPagerAdapter(card));
		viewPager.setCurrentItem(position);

		return rootView;
	}

	private MagicCardViewPager getViewPager(MagicCard card, View rootView) {
		MagicCardViewPager viewPager = ((MagicCardViewPager) rootView
				.findViewById(R.id.pager)).setCard(card);
		return viewPager;
	}

	private PagerAdapter getPagerAdapter(MagicCard card) {
		return new MagicCardPagerAdapter(getFragmentManager()).setCard(card);
	}
}
