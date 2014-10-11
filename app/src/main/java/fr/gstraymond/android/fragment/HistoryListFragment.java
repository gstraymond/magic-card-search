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
import fr.gstraymond.db.History;
import fr.gstraymond.ui.adapter.HistoryArrayAdapter;

import static fr.gstraymond.constants.Consts.HISTORY_LIST;

public class HistoryListFragment extends ListFragment {

    public static final String EMPTY = "empty";

    private List<Map<String, String>> messages;
    private ArrayList<History> allHistory;

    private void initEmptyMsg() {
        messages = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
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
            arrayAdapter = new HistoryArrayAdapter(getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text2, allHistory);
        }

        setListAdapter(arrayAdapter);
    }


    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        Bundle bundle = new Bundle();
        bundle.putParcelable("history", allHistory.get(position));
        Intent intent = new Intent();
        intent.putExtras(bundle);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
