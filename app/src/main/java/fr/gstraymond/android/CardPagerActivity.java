package fr.gstraymond.android;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import fr.gstraymond.R;
import fr.gstraymond.android.fragment.CardPagerFragment;

import static fr.gstraymond.constants.Consts.POSITION;

public class CardPagerActivity extends CardCommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        replaceFragment(new CardPagerFragment(), R.id.card_pager_container, getBundle());
    }

    @Override
    protected Bundle getBundle() {
        Bundle bundle = super.getBundle();
        bundle.putInt(POSITION, getPosition());
        return bundle;
    }

    private int getPosition() {
        return getIntent().getIntExtra(POSITION, 0);
    }

    @Override
    public void replaceFragment(Fragment fragment, int id) {
        if (getFragmentManager().findFragmentById(id) == null) {
            super.replaceFragment(fragment, id);
        }
    }
}
