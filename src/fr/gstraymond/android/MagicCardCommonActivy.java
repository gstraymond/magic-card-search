package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.MAGIC_CARD;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.tools.LanguageUtil;

public abstract class MagicCardCommonActivy extends Activity {

	private MagicCard card;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		card = getCard(savedInstanceState);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
	
		setTitle(getFullTitle(card));
	}

	protected Bundle getBundle() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(MAGIC_CARD, card);
		return bundle;
	}
	
	private MagicCard getCard(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			Parcelable savedCard = savedInstanceState.getParcelable(MAGIC_CARD);
			if (savedCard != null) {
				return savedInstanceState.getParcelable(MAGIC_CARD);
			}
		}
		return getIntent().getParcelableExtra(MAGIC_CARD);
	}

	private String getFullTitle(MagicCard card) {
		if (LanguageUtil.showFrench(this) && card.getFrenchTitle() != null) {
			return card.getFrenchTitle();
		}
		
		return card.getTitle();
	}
	
	protected void replaceFragment(Fragment fragment, int id) {
		getFragmentManager().beginTransaction().replace(id, fragment).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(MAGIC_CARD, card);
	}


	protected MagicCard getCard() {
		return card;
	}
}
