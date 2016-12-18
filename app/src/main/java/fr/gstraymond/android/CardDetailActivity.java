package fr.gstraymond.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import fr.gstraymond.R;
import fr.gstraymond.android.fragment.CardDetailFragment;
import fr.gstraymond.models.response.Card;
import fr.gstraymond.tools.LanguageUtil;

import static fr.gstraymond.constants.Consts.CARD;
import static fr.gstraymond.constants.Consts.POSITION;

public class CardDetailActivity extends CardCommonActivity implements
        CardDetailFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(formatTitle(this, getCard()));

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
        Intent intent = new Intent(this, CardPagerActivity.class);
        intent.putExtra(CARD, getCard());
        // first element is a card
        intent.putExtra(POSITION, id - 1);
        startActivity(intent);
    }

    public static String formatTitle(Context context, Card card) {
        if (LanguageUtil.showFrench(context) && card.getFrenchTitle() != null) {
            return String.format("%s (%s)", card.getFrenchTitle(), card.getTitle());
        }

        return card.getTitle();
    }
}
