package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.POSITION;
import android.app.Fragment;
import android.os.Bundle;
import fr.gstraymond.R;

public class MagicCardPagerActivity extends MagicCardCommonActivy {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magiccard_pager);


		Bundle bundle = getBundle();
		
		Fragment fragment = new MagicCardPagerFragment();
		fragment.setArguments(bundle);
		replaceFragment(fragment, R.id.magiccard_pager_container);
	}

	@Override
	protected Bundle getBundle() {
		Bundle bundle = super.getBundle();
		bundle.putInt(POSITION, getPosition());
		return bundle;
	}
	
	private int getPosition() {
		return getIntent().getIntExtra(POSITION, 0);
	}
}
