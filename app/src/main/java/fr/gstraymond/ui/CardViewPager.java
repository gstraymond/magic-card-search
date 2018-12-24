package fr.gstraymond.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import fr.gstraymond.R;
import fr.gstraymond.android.fragment.CardPagerFragment;
import fr.gstraymond.models.search.response.Card;
import fr.gstraymond.models.search.response.Publication;
import android.support.v4.app.Fragment;

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
        if (!getTitle().startsWith(itemIdDisplayed)) {
            Publication publication = card.getPublications().get(itemId);
            String text = publication.getEdition() + " — " + publication.getRarity();

            int count = getAdapter().getCount();
            if (count > 1) {
                text = itemIdDisplayed + "/" + count + " " + text;
            }

            getActivity().setTitle(text);

            FragmentActivity activity = (FragmentActivity) getActivity();

            Fragment fragment = activity.getSupportFragmentManager().findFragmentById(R.id.card_pager_container);
            if (fragment instanceof CardPagerFragment) {
                ((CardPagerFragment) fragment).setPosition(itemId);
            }
        }
        super.computeScroll();
    }

    private String getTitle() {
        return getActivity().getTitle().toString();
    }

    private Activity getActivity() {
        return (Activity) getContext();
    }

    public CardViewPager setCard(Card card) {
        this.card = card;
        return this;
    }
}
