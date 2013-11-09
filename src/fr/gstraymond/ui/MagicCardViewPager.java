package fr.gstraymond.ui;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.TextView;

public class MagicCardViewPager extends ViewPager {

	private List<String> publications;
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
			String publication = publications.get(itemId);
			String text = publication.split("\\|")[0];
			if (count > 1) {
				text += " — " + itemIdDisplayed + "/" + count;
			}
			publicationTextView.setText(text);
		}
		super.computeScroll();
	}

	public MagicCardViewPager setPublications(List<String> publications) {
		this.publications = publications;
		return this;
	}

	public MagicCardViewPager setPublicationTextView(TextView publicationTextView) {
		this.publicationTextView = publicationTextView;
		return this;
	}
}
