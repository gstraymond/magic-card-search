package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.HISTORY_LIST;

import java.util.ArrayList;

import android.os.Bundle;
import fr.gstraymond.R;
import fr.gstraymond.android.fragment.HistoryListFragment;
import fr.gstraymond.db.History;
import fr.gstraymond.db.HistoryDataSource;

public class HistoryActivity extends CustomActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		HistoryDataSource historyDataSource = new HistoryDataSource(this);
		ArrayList<History> allHistory = historyDataSource.getAllHistory();
		
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(HISTORY_LIST, allHistory);
		
		replaceFragment(new HistoryListFragment(), R.id.history_fragment, bundle);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

}
