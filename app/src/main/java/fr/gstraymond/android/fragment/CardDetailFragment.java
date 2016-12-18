package fr.gstraymond.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.gstraymond.models.search.response.Card;
import fr.gstraymond.ui.adapter.SetArrayAdapter;

import static fr.gstraymond.constants.Consts.CARD;

public class CardDetailFragment extends CustomListFragment {

    private Callbacks callbacks = dummyCallbacks;

    public interface Callbacks {
        void onItemSelected(int id);
    }

    private static Callbacks dummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Card card = getArguments().getParcelable(CARD);

        List<Object> objects = new ArrayList<>();
        objects.add(card);
        objects.addAll(card.getPublications());

        ListAdapter arrayAdapter = new SetArrayAdapter(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text2,
                objects,
                new SetArrayAdapter.Callbacks() {
                    @Override
                    public void onImageClick(int position) {
                        callbacks.onItemSelected(position + 1);
                    }
                });
        setListAdapter(arrayAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setDividerHeight(0);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        callbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = dummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position,
                                long id) {
        super.onListItemClick(listView, view, position, id);
        callbacks.onItemSelected(position);
    }
}
