package fr.gstraymond.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import fr.gstraymond.R;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.ui.CastingCostAssetLoader;
import fr.gstraymond.ui.MagicCardArrayAdapter;

public class MagicCardListFragment extends ListFragment {

	public static String CARDS = "cards";
	public static String TOTAL_CARD_COUNT = "totalCardCount";
	private List<MagicCard> cards; 
	private int totalCardCount;
	private boolean twoPaneMode;

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private Callbacks mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;
	private ArrayAdapter<MagicCard> arrayAdapter;

	public interface Callbacks {
		public void onItemSelected(Parcelable id);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(Parcelable id) {
		}
	};

	public MagicCardListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getArguments() != null && getArguments().getParcelableArrayList(CARDS) != null) {
			cards = getArguments().getParcelableArrayList(CARDS);
			
			if (getArguments().getInt(TOTAL_CARD_COUNT) != 0) {
				totalCardCount = getArguments().getInt(TOTAL_CARD_COUNT);
			}
		} else {
			cards = new ArrayList<MagicCard>();
		}


		if (getActivity().findViewById(R.id.magiccard_detail_container) != null) {
			twoPaneMode = true;
		}

		CustomApplication applicationContext = (CustomApplication) getActivity().getApplicationContext();
		CastingCostAssetLoader castingCostAssetLoader = applicationContext.getCastingCostAssetLoader();
		
		arrayAdapter = new MagicCardArrayAdapter(getActivity(),
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

		MagicCardListActivity activity = (MagicCardListActivity) getActivity();
		getListView().setOnScrollListener(activity.getEndScrollListener());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		
		mCallbacks.onItemSelected(cards.get(position));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	public void appendCards(List<MagicCard> cards) {
		arrayAdapter.addAll(cards);
		arrayAdapter.notifyDataSetChanged();
	}
	
	public int getCardListCount() {
		return arrayAdapter.getCount();
	}

	public int getTotalCardCount() {
		return totalCardCount;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// select the first element
		if (twoPaneMode && getListAdapter().getCount() > 0) {
			long itemId = getListAdapter().getItemId(0);
			View view = getListAdapter().getView(0, null, null);
			getListView().performItemClick(view, 0, itemId);	
		}
	}
}
