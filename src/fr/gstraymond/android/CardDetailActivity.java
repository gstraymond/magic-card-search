package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.CARD;
import static fr.gstraymond.constants.Consts.POSITION;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amazon.device.associates.AssociatesAPI;
import com.amazon.device.associates.LinkService;
import com.amazon.device.associates.NotInitializedException;
import com.amazon.device.associates.OpenSearchPageRequest;

import fr.gstraymond.R;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.tools.LanguageUtil;

public class CardDetailActivity extends CardCommonActivy implements
		CardDetailFragment.Callbacks {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_detail);
		AssociatesAPI.initialize(new AssociatesAPI.Config(
				"77efbb6760054935b8969a20c12be781", this));

		Bundle bundle = getBundle();

		TextView titleTextView = (TextView) findViewById(R.id.card_detail_title);
		titleTextView.setText(formatTitle(this, getCard()));

		ImageButton button = (ImageButton) findViewById(R.id.array_adapter_buy_button);
		button.setEnabled(true);
		final String searchTerm = "mtg " + getCard().getTitle();
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				OpenSearchPageRequest request = new OpenSearchPageRequest(
						searchTerm);
				try {
					LinkService linkService = AssociatesAPI.getLinkService();
					linkService.openRetailPage(request);
				} catch (NotInitializedException e) {
					e.printStackTrace();
				}
			}
		});

		Fragment fragment = new CardDetailFragment();
		fragment.setArguments(bundle);
		replaceFragment(fragment, R.id.card_detail_container);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.pictures_tab:
			Intent intent = new Intent(this, CardPagerActivity.class);
			intent.putExtra(CARD, getCard());
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.card_detail_menu, menu);
		return true;
	}

	@Override
	public void onItemSelected(int id) {
		// FIXME a refactorer avec CardListActivity
		if (isTablet()) {
			Bundle bundle = new Bundle();
			bundle.putParcelable(CARD, getCard());
			// first element is a card
			bundle.putInt(POSITION, id - 1);

			CardPagerFragment fragment = new CardPagerFragment();
			fragment.setArguments(bundle);
			getFragmentManager().beginTransaction()
					.replace(R.id.card_detail_container, fragment).commit();
		} else {
			Intent intent = new Intent(this, CardPagerActivity.class);
			intent.putExtra(CARD, getCard());
			// first element is a card
			intent.putExtra(POSITION, id - 1);
			startActivity(intent);
		}
	}

	public static String formatTitle(Context context, Card card) {
		if (LanguageUtil.showFrench(context) && card.getFrenchTitle() != null) {
			return card.getFrenchTitle() + "\n(" + card.getTitle() + ")";
		}

		return card.getTitle();
	}

	private boolean isTablet() {
		CustomApplication application = (CustomApplication) getApplication();
		return application.isTablet();
	}
}
