package fr.gstraymond.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import fr.gstraymond.R;
import fr.gstraymond.magicsearch.model.response.MagicCard;

public class MagicCardDetailActivity extends FragmentActivity {
	
	private Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magiccard_detail);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			MagicCard card = getIntent().getParcelableExtra(MagicCardDetailFragment.MAGIC_CARD);
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putParcelable(MagicCardDetailFragment.MAGIC_CARD, card);
			
			MagicCardDetailFragment fragment = new MagicCardDetailFragment();
			fragment.setArguments(arguments);
			
			getSupportFragmentManager().beginTransaction()
					.add(R.id.magiccard_detail_container, fragment).commit();
			
			String frenchTitle = card.getFrenchTitle() != null ? " / " + card.getFrenchTitle() : "";
			setTitle(card.getTitle() + frenchTitle);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				finish();
				return true;

			case R.id.oracle_tab :
				findViewById(R.id.pictures_layout).setVisibility(View.GONE);
				findViewById(R.id.magiccard_detail).setVisibility(View.VISIBLE);
				item.setEnabled(false);
				menu.findItem(R.id.pictures_tab).setEnabled(true);
				return true;
			
			case R.id.pictures_tab :
				findViewById(R.id.magiccard_detail).setVisibility(View.GONE);
				findViewById(R.id.pictures_layout).setVisibility(View.VISIBLE);
				item.setEnabled(false);	
				menu.findItem(R.id.oracle_tab).setEnabled(true);
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
}
