package com.magic.card.search.browser;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.magic.card.search.browser.adapter.CardsAdapter;
import com.magic.card.search.browser.android.CustomApplication;
import com.magic.card.search.browser.zip.ZipDatabase;
import com.magic.card.search.commons.log.Log;
import com.magic.card.search.commons.model.MTGCard;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CardsAdapter.CardClickListener {

    private ZipDatabase zipDatabase;
    private MainDataPresenter dataPresenter = new MainDataPresenter(this, this);
    private Log log = new Log(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zipDatabase = new ZipDatabase(getCustomApplication());

        Toolbar toolbar = getToolbar();
        setSupportActionBar(toolbar);

        FloatingActionButton fab = getFab();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = getDrawerLayout();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getNavigationView().setNavigationItemSelectedListener(this);

        dataPresenter.bind();
    }

    private void readZip() {
        NavigationView navigationView = getNavigationView();

        List<String> allEntries = zipDatabase.getAllEntries();
        for (String entry : allEntries) {
            navigationView.getMenu().add(entry);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        readZip();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = getDrawerLayout();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String entryName = item.getTitle().toString();
        List<MTGCard> cards = zipDatabase.getCards(entryName);
        log.d(String.format("number of cards found %s for %s", cards.size(), entryName));
        dataPresenter.setCards(cards);
        getDrawerLayout().closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCardClick(MTGCard mtgCard) {
        startActivity(CardActivity.getIntent(this, mtgCard));
    }

    private DrawerLayout getDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private NavigationView getNavigationView() {
        return (NavigationView) findViewById(R.id.nav_view);
    }

    private FloatingActionButton getFab() {
        return (FloatingActionButton) findViewById(R.id.fab);
    }

    private Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    private CustomApplication getCustomApplication() {
        return (CustomApplication) getApplication();
    }
}
