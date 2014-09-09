package fr.gstraymond.android;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import fr.gstraymond.R;
import fr.gstraymond.android.fragment.HistoryListFragment;
import fr.gstraymond.db.History;
import fr.gstraymond.db.HistoryDataSource;

import static fr.gstraymond.constants.Consts.HISTORY_LIST;

public class HistoryActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        HistoryDataSource historyDataSource = new HistoryDataSource(this);
        showHistory(historyDataSource);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.history_clear_tab:
                HistoryDataSource historyDataSource = new HistoryDataSource(this);
                historyDataSource.clearHistory();
                showHistory(historyDataSource);
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

    private void showHistory(HistoryDataSource historyDataSource) {
        ArrayList<History> allHistory = historyDataSource.getAllHistory();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(HISTORY_LIST, allHistory);

        replaceFragment(new HistoryListFragment(), R.id.history_fragment, bundle);
    }
}
