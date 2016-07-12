package fr.gstraymond.android.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.gstraymond.R;
import fr.gstraymond.android.CustomActivity;
import fr.gstraymond.db.json.JsonHistory;
import fr.gstraymond.db.json.JsonHistoryDataSource;
import fr.gstraymond.ui.adapter.HistoryArrayAdapter;

import static fr.gstraymond.constants.Consts.HISTORY_LIST;

public class HistoryListFragment extends CustomListFragment {

    public static final String EMPTY = "empty";

    private List<Map<String, String>> messages;
    private ArrayList<JsonHistory> allHistory;

    private void initEmptyMsg() {
        messages = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(EMPTY, getResources().getString(R.string.history_empty));
        messages.add(map);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEmptyMsg();

        allHistory = getArguments().getParcelableArrayList(HISTORY_LIST);

        ListAdapter arrayAdapter;
        if (allHistory.isEmpty()) {
            arrayAdapter = new SimpleAdapter(this.getActivity(),
                    messages, android.R.layout.simple_list_item_1, new String[]{EMPTY},
                    new int[]{android.R.id.text1});
        } else {
            JsonHistoryDataSource jsonHistoryDataSource = ((CustomActivity) getActivity()).getJsonHistoryDataSource();
            arrayAdapter = new HistoryArrayAdapter(getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text2, allHistory, jsonHistoryDataSource);
        }

        setListAdapter(arrayAdapter);
    }


    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        if (allHistory == null) return;

        Bundle bundle = new Bundle();
        bundle.putParcelable("history", allHistory.get(position));
        Intent intent = new Intent();
        intent.putExtras(bundle);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
