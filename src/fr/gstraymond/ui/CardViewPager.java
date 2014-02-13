package fr.gstraymond.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.search.model.response.Publication;

public class CardViewPager extends ViewPager {

	private Card card;
	
	public CardViewPager(Context context) {
		super(context);
	}

	public CardViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void computeScroll() {
		int itemId = getCurrentItem();
		String itemIdDisplayed = (itemId + 1) + "";
		if (! getActivity().getTitle().toString().startsWith(itemIdDisplayed)) {
			Publication publication = card.getPublications().get(itemId);
			String text = publication.getEdition() + " — " + publication.getRarity();

			int count = getAdapter().getCount();
			if (count > 1) {
				text = itemIdDisplayed + "/" + count + " " + text;
			}
			
			getActivity().setTitle(text);
		}
		super.computeScroll();
	}
	
	private Activity getActivity() {
		return (Activity) getContext();
	}

	public CardViewPager setCard(Card card) {
		this.card = card;
		return this;
	}
}
