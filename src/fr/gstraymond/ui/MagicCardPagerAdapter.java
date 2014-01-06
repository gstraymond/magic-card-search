package fr.gstraymond.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.magicsearch.model.response.Publication;

public class MagicCardPagerAdapter extends FragmentStatePagerAdapter {
	
	private MagicCard card;

	public MagicCardPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Publication publication = card.getPublications().get(position);
		return new CardFragment().setCardUrl(publication.getImage());
	}

	@Override
	public int getCount() {
		return card.getPublications().size();
	}

	public MagicCardPagerAdapter setCard(MagicCard card) {
		this.card = card;
		return this;
	}
}
