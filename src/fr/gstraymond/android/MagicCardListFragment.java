package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.MAGIC_CARD_LIST;

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

	private List<MagicCard> cards; 
	private boolean twoPaneMode;

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private Callbacks callbacks = dummyCallbacks;
	private int position = ListView.INVALID_POSITION;
	private ArrayAdapter<MagicCard> arrayAdapter;

	public interface Callbacks {
		public void onItemSelected(Parcelable id);
	}

	private static Callbacks dummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(Parcelable id) {
		}
	};

	public MagicCardListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		cards = getArguments().getParcelableArrayList(MAGIC_CARD_LIST);

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
		callbacks.onItemSelected(cards.get(position));
//		getListView().setItemChecked(position, true);
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
	
	public void appendCards(List<MagicCard> cards) {
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
		if (twoPaneMode && getListAdapter().getCount() > 0) {
//			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			
			long itemId = getListAdapter().getItemId(0);
			View view = getListAdapter().getView(0, null, null);
			getListView().performItemClick(view, 0, itemId);	
		}
	}
}
