package fr.gstraymond.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import fr.gstraymond.R;
import fr.gstraymond.android.fragment.HistoryListFragment;
import fr.gstraymond.db.json.JsonHistory;

import static fr.gstraymond.constants.Consts.HISTORY_LIST;

public class HistoryActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showHistory();

        actionBarSetDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.history_clear_tab:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(getString(R.string.history_alert_title));
                alert.setMessage(getString(R.string.history_alert_description));

                alert.setPositiveButton(getString(R.string.history_alert_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getJsonHistoryDataSource().clearNonFavoriteHistory();
                        showHistory();
                    }
                });
                alert.setNegativeButton(getString(R.string.history_alert_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //So sth here when "cancel" clicked.
                    }
                });
                alert.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.card_history_menu, menu);
        return true;
    }

    private void showHistory() {
        ArrayList<JsonHistory> allHistory = getJsonHistoryDataSource().getAllHistory();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(HISTORY_LIST, allHistory);

        replaceFragment(new HistoryListFragment(), R.id.history_fragment, bundle);
    }
}
