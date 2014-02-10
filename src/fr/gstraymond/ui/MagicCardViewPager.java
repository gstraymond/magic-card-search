package fr.gstraymond.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.magicsearch.model.response.Publication;

public class MagicCardViewPager extends ViewPager {

	private MagicCard card;
	
	public MagicCardViewPager(Context context) {
		super(context);
	}

	public MagicCardViewPager(Context context, AttributeSet attrs) {
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

	public MagicCardViewPager setCard(MagicCard card) {
		this.card = card;
		return this;
	}
}
