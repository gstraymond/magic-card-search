package com.magic.card.search.browser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.magic.card.search.commons.model.MTGCard;

public class CardActivity extends AppCompatActivity {

    private static String EXTRA_CARD = "EXTRA_CARD";

    public static Intent getIntent(Context context, MTGCard mtgCard) {
        Intent intent = new Intent(context, CardActivity.class);
        return intent.putExtra(EXTRA_CARD, mtgCard);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = getToolbar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = getFab();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private FloatingActionButton getFab() {
        return (FloatingActionButton) findViewById(R.id.fab);
    }

    private Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }
}
