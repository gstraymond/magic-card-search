package fr.gstraymond.ui;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.TextView;
import fr.gstraymond.magicsearch.model.response.Publication;

public class MagicCardViewPager extends ViewPager {

	private List<Publication> publications;
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
			Publication publication = publications.get(itemId);
			String text = publication.getEdition() + " (" + publication.getRarity() + ")";
			if (count > 1) {
				text += " — " + itemIdDisplayed + "/" + count;
			}
			publicationTextView.setText(text);
		}
		super.computeScroll();
	}

	public MagicCardViewPager setPublications(List<Publication> publications) {
		this.publications = publications;
		return this;
	}

	public MagicCardViewPager setPublicationTextView(TextView publicationTextView) {
		this.publicationTextView = publicationTextView;
		return this;
	}
}
