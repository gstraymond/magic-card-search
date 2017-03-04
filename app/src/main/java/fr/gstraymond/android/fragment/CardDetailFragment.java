package fr.gstraymond.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import fr.gstraymond.android.CustomActivity;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.models.Deck;
import fr.gstraymond.models.search.response.Card;
import fr.gstraymond.ui.adapter.card.detail.CardDetailAdapter;
import fr.gstraymond.utils.CardIdUtilsKt;

import static fr.gstraymond.constants.Consts.CARD;

public class CardDetailFragment extends CustomListFragment {

    private Callbacks callbacks = dummyCallbacks;
    private List<Object> objects;

    public interface Callbacks {
        void onItemSelected(int id);

        void onListSelected(String list);
    }

    private static Callbacks dummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id) {
        }

        @Override
        public void onListSelected(String list) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Card card = getArguments().getParcelable(CARD);

        CustomActivity activity = (CustomActivity) getActivity();
        final CustomApplication customApplication = (CustomApplication) activity.getApplication();

        String id = CardIdUtilsKt.getId(card);
        List<String> listIds = customApplication.getListsCardId().get(id);

        objects = new ArrayList<>();
        objects.add(card);
        if (listIds != null) objects.addAll(listIds);
        objects.addAll(card.getPublications());

        ListAdapter arrayAdapter = new CardDetailAdapter(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text2,
                objects,
                new CardDetailAdapter.Callbacks() {
                    @Override
                    public void onImageClick(int position) {
                        callbacks.onItemSelected(position);
                    }

                    @Nullable
                    @Override
                    public Deck getDeck(@NotNull String deckId) {
                        return customApplication.deckList.getByUid(deckId);
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
    public void onListItemClick(ListView listView,
                                View view,
                                int position,
                                long id) {
        Object object = objects.get(position);
        if (object instanceof String) {
            callbacks.onListSelected((String) object);
        } else {
            callbacks.onItemSelected(position - 1);
        }

    }
}
