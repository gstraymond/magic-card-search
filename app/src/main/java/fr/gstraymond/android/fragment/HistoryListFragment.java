package fr.gstraymond.android.fragment;

import static fr.gstraymond.constants.Consts.HISTORY_LIST;

import java.util.ArrayList;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ListAdapter;
import fr.gstraymond.db.History;
import fr.gstraymond.ui.adapter.HistoryArrayAdapter;

public class HistoryListFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ArrayList<History> allHistory = getArguments()
				.getParcelableArrayList(HISTORY_LIST);

		ListAdapter arrayAdapter = new HistoryArrayAdapter(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text2, allHistory);
		setListAdapter(arrayAdapter);
	}
}
