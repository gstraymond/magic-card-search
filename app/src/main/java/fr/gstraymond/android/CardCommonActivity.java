package fr.gstraymond.android;

import android.os.Bundle;
import android.view.MenuItem;

import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.tools.LanguageUtil;

import static fr.gstraymond.constants.Consts.CARD;

public abstract class CardCommonActivity extends CustomActivity {

    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        card = getIntent().getParcelableExtra(CARD);

        actionBarSetDisplayHomeAsUpEnabled(true);

        setTitle(getFullTitle(card));
    }

    protected Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CARD, card);
        return bundle;
    }

    private String getFullTitle(Card card) {
        if (LanguageUtil.showFrench(this) && card.getFrenchTitle() != null) {
            return card.getFrenchTitle();
        }

        return card.getTitle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    protected Card getCard() {
        return card;
    }
}
