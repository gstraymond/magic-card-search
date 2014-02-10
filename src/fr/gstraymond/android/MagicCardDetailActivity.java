package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.MAGIC_CARD;
import static fr.gstraymond.constants.Consts.POSITION;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.tools.ActivityUtil;
import fr.gstraymond.tools.LanguageUtil;

public class MagicCardDetailActivity extends MagicCardCommonActivy implements
		MagicCardDetailFragment.Callbacks {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magiccard_detail);

		Bundle bundle = getBundle();

		TextView titleTextView = (TextView) findViewById(R.id.magiccard_detail_title);
		titleTextView.setText(formatTitle(this, getCard()));

		Fragment detailFragment = new MagicCardDetailFragment();
		detailFragment.setArguments(bundle);
		replaceFragment(detailFragment, R.id.magiccard_detail_container);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.pictures_tab:
			Intent intent = ActivityUtil.getIntent(this, MagicCardPagerActivity.class);
			intent.putExtra(MAGIC_CARD, getCard());
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.magiccard_detail_menu, menu);
		return true;
	}

	@Override
	public void onItemSelected(int id) {
		Intent intent = ActivityUtil.getIntent(this, MagicCardPagerActivity.class);
		intent.putExtra(MAGIC_CARD, getCard());
		// first element is a card
		intent.putExtra(POSITION, id - 1);
		startActivity(intent);
	}

	public static String formatTitle(Context context, MagicCard card) {
		if (LanguageUtil.showFrench(context) && card.getFrenchTitle() != null) {
			return card.getFrenchTitle() + "\n(" + card.getTitle() + ")";
		}
		
		return card.getTitle();
	}
}
