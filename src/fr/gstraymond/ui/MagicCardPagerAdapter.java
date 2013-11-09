package fr.gstraymond.ui;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class MagicCardPagerAdapter extends FragmentPagerAdapter {
	
	private List<String> publications;

	public MagicCardPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		String publication = publications.get(position);
		Log.d(getClass().getName(), "publication " + publication);
		return new CardFragment().setCardUrl(publication.split("\\|")[1]);
	}

	@Override
	public int getCount() {
		return publications.size();
	}

	public MagicCardPagerAdapter setPublications(List<String> publications) {
		this.publications = publications;
		return this;
	}
}
