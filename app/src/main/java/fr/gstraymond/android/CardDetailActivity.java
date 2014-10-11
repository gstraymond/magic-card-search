package fr.gstraymond.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import fr.gstraymond.R;
import fr.gstraymond.android.fragment.CardDetailFragment;
import fr.gstraymond.android.fragment.CardPagerFragment;
import fr.gstraymond.android.tools.amazon.AmazonUtils;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.tools.LanguageUtil;

import static fr.gstraymond.constants.Consts.CARD;
import static fr.gstraymond.constants.Consts.POSITION;

public class CardDetailActivity extends CardCommonActivy implements
        CardDetailFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        AmazonUtils.initAmazonApi(this);

        TextView titleTextView = (TextView) findViewById(R.id.card_detail_title);
        titleTextView.setText(formatTitle(this, getCard()));

        replaceFragment(new CardDetailFragment(), R.id.card_detail_container, getBundle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.pictures_tab:
                Intent intent = new Intent(this, CardPagerActivity.class);
                intent.putExtra(CARD, getCard());
                startActivity(intent);
                return true;

            case R.id.buy_tab:
                AmazonUtils.openSearch(this, getCard());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.card_detail_menu, menu);
        return true;
    }

    @Override
    public void onItemSelected(int id) {
        // FIXME a refactorer avec CardListActivity
        if (isTablet()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(CARD, getCard());
            // first element is a card
            bundle.putInt(POSITION, id - 1);

            replaceFragment(new CardPagerFragment(), R.id.card_detail_container, bundle);
        } else {
            Intent intent = new Intent(this, CardPagerActivity.class);
            intent.putExtra(CARD, getCard());
            // first element is a card
            intent.putExtra(POSITION, id - 1);
            startActivity(intent);
        }
    }

    public static String formatTitle(Context context, Card card) {
        if (LanguageUtil.showFrench(context) && card.getFrenchTitle() != null) {
            return card.getFrenchTitle() + "\n(" + card.getTitle() + ")";
        }

        return card.getTitle();
    }
}
