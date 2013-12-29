package fr.gstraymond.android;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import fr.gstraymond.R;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.tools.LanguageUtil;

public class MagicCardDetailActivity extends FragmentActivity {

	private static final String MAGIC_CARD = "magicCard";
	private Menu menu;
	private MagicCard magicCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magiccard_detail);

		if (savedInstanceState != null) {
			Parcelable savedCard = savedInstanceState.getParcelable(MAGIC_CARD);
			if (savedCard != null) {
				magicCard = savedInstanceState.getParcelable(MAGIC_CARD);
			}
		} else {
			magicCard = getIntent().getParcelableExtra(MagicCardDetailFragment.MAGIC_CARD);
		}

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle arguments = new Bundle();
		arguments.putParcelable(MagicCardDetailFragment.MAGIC_CARD, magicCard);

		MagicCardDetailFragment fragment = new MagicCardDetailFragment();
		fragment.setArguments(arguments);

		getSupportFragmentManager().beginTransaction().add(R.id.magiccard_detail_container, fragment).commit();

		setTitle(getFullTitle(magicCard));
	}

	private String getFullTitle(MagicCard card) {
		if (LanguageUtil.showFrench(this) && card.getFrenchTitle() != null) {
			return card.getFrenchTitle();
		}
		
		return card.getTitle();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		case R.id.oracle_tab:
			findViewById(R.id.pictures_layout).setVisibility(View.GONE);
			findViewById(R.id.magiccard_detail).setVisibility(View.VISIBLE);
			item.setVisible(false);
			menu.findItem(R.id.pictures_tab).setVisible(true);
			return true;

		case R.id.pictures_tab:
			findViewById(R.id.magiccard_detail).setVisibility(View.GONE);
			findViewById(R.id.pictures_layout).setVisibility(View.VISIBLE);
			item.setVisible(false);
			menu.findItem(R.id.oracle_tab).setVisible(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.magiccard_detail_menu, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(MAGIC_CARD, magicCard);
	}
}
