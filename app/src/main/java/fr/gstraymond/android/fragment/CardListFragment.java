package fr.gstraymond.android.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.ui.CastingCostAssetLoader;
import fr.gstraymond.ui.adapter.CardArrayAdapter;

import static fr.gstraymond.constants.Consts.CARD_LIST;

public class CardListFragment extends ListFragment {

    private List<Card> cards;

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private Callbacks callbacks = dummyCallbacks;
    private int position = ListView.INVALID_POSITION;
    private ArrayAdapter<Card> arrayAdapter;

    public interface Callbacks {
        void onItemSelected(Parcelable id);
    }

    private static Callbacks dummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Parcelable id) {
        }
    };

    public CardListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cards = getArguments().getParcelableArrayList(CARD_LIST);

        CastingCostAssetLoader castingCostAssetLoader = getCustomApplication()
                .getCastingCostAssetLoader();

        arrayAdapter = new CardArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text2, cards, castingCostAssetLoader);

        setListAdapter(arrayAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            int position = savedInstanceState.getInt(STATE_ACTIVATED_POSITION);
            setActivatedPosition(position);
        }

        CardListActivity activity = (CardListActivity) getActivity();
        getListView().setOnScrollListener(activity.getEndScrollListener());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Activities containing this fragment must implement its callbacks.
        if (!(context instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        callbacks = (Callbacks) context;
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
        callbacks.onItemSelected(cards.get(position));
        // getListView().setItemChecked(position, true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (position != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, position);
        }
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(position, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        this.position = position;
    }

    public void appendCards(List<Card> cards) {
        if (arrayAdapter == null) return;
        
        arrayAdapter.addAll(cards);
        arrayAdapter.notifyDataSetChanged();
    }

    public int getCardListCount() {
        return arrayAdapter.getCount();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // select the first element
        if (isTablet() && getListAdapter().getCount() > 0) {
            long itemId = getListAdapter().getItemId(0);
            View view = getListAdapter().getView(0, null, getListView());
            getListView().performItemClick(view, 0, itemId);
        }
    }

    private CustomApplication getCustomApplication() {
        return (CustomApplication) getActivity().getApplication();
    }

    private boolean isTablet() {
        return getCustomApplication().isTablet();
    }
}
