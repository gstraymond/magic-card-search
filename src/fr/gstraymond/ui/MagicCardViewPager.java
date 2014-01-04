package fr.gstraymond.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.TextView;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.magicsearch.model.response.Publication;

public class MagicCardViewPager extends ViewPager {

	private MagicCard card;
	private TextView publicationTextView;
	
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
		if (! publicationTextView.getText().toString().startsWith(itemIdDisplayed)) {
			int count = getAdapter().getCount();
			Publication publication = card.getPublications().get(itemId);
			String text = publication.getEdition() + " (" + publication.getRarity() + ")";
			if (count > 1) {
				text += " — " + itemIdDisplayed + "/" + count;
			}
			publicationTextView.setText(text);
		}
		super.computeScroll();
	}

	public MagicCardViewPager setPublicationTextView(TextView publicationTextView) {
		this.publicationTextView = publicationTextView;
		return this;
	}

	public MagicCardViewPager setCard(MagicCard card) {
		this.card = card;
		return this;
	}
}
