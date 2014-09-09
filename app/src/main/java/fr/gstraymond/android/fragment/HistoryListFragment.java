package fr.gstraymond.android.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ListAdapter;
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

    private void initEmptyMsg() {
        messages = new ArrayList<Map<String, String>>();
        Map map = new HashMap<String, String>();
        map.put(EMPTY, getResources().getString(R.string.history_empty));
        messages.add(map);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEmptyMsg();

        ArrayList<History> allHistory = getArguments()
                .getParcelableArrayList(HISTORY_LIST);

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
}
