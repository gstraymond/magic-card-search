package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.MAGIC_CARD;
import static fr.gstraymond.constants.Consts.POSITION;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import fr.gstraymond.R;
import fr.gstraymond.tools.ActivityUtil;

public class MagicCardDetailActivity extends MagicCardCommonActivy implements
		SetListFragment.Callbacks {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magiccard_detail);

		Bundle bundle = getBundle();

		Fragment detailFragment = new MagicCardDetailFragment();
		detailFragment.setArguments(bundle);
		replaceFragment(detailFragment, R.id.magiccard_detail_container);

		Fragment setListFragment = new SetListFragment();
		setListFragment.setArguments(bundle);
		replaceFragment(setListFragment, R.id.magiccard_set_list);
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
		intent.putExtra(POSITION, id);
		startActivity(intent);
	}
}
