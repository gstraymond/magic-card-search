package fr.gstraymond.ui;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import fr.gstraymond.magicsearch.model.response.Publication;

public class MagicCardPagerAdapter extends FragmentStatePagerAdapter {
	
	private List<Publication> publications;

	public MagicCardPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Publication publication = publications.get(position);
		return new CardFragment().setCardUrl(publication.getImage());
	}

	@Override
	public int getCount() {
		return publications.size();
	}

	public MagicCardPagerAdapter setPublications(List<Publication> publications) {
		this.publications = publications;
		return this;
	}
}
