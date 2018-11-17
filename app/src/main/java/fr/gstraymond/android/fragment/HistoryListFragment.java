package fr.gstraymond.android.fragment;

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
import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.android.CustomActivity;
import fr.gstraymond.android.HistoryActivity;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.db.json.HistoryList;
import fr.gstraymond.models.History;
import fr.gstraymond.ui.adapter.HistoryArrayAdapter;

import static fr.gstraymond.constants.Consts.HISTORY_LIST;

public class HistoryListFragment extends CustomListFragment {

    public static final String EMPTY = "empty";

    private List<Map<String, String>> messages;
    private ArrayList<History> allHistory;

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
            HistoryList historyList = ((CustomActivity) getActivity()).getJsonHistoryDataSource();
            arrayAdapter = new HistoryArrayAdapter(getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text2, allHistory, historyList);
        }

        setListAdapter(arrayAdapter);
    }


    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        if (allHistory == null) return;

        History history = allHistory.get(position);

        SearchOptions currentSearch = new SearchOptions()
                .updateQuery(history.getQuery())
                .updateFacets(history.getFacets());

        String deckId = getArguments().getString(HistoryActivity.Companion.getDECK_ID_EXTRA());
        if (deckId != null) {
            currentSearch.setDeckId(deckId);
        }
        currentSearch.setAddToSideboard(getArguments().getBoolean(HistoryActivity.Companion.getADD_TO_SIDEBOARD_EXTRA()));

        Intent intent = CardListActivity.Companion.getIntent(getActivity(), currentSearch);
        startActivity(intent);
        getActivity().finish();
    }
}
