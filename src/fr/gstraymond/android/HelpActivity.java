package fr.gstraymond.android;

import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gstraymond.R;
import fr.gstraymond.biz.CastingCostImageGetter;
import fr.gstraymond.magicsearch.help.HelpText;
import fr.gstraymond.tools.HelpFormatter;
import fr.gstraymond.tools.MapperUtil;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		CastingCostImageGetter imageGetter = new CastingCostImageGetter(getCustomApplication().getCastingCostAssetLoader());
		HelpFormatter formatter = new HelpFormatter(imageGetter);
		
		Spanned text = formatter.format(getHelpText());

		TextView view = getTextView();
		view.setText(text);
		// rends les liens cliquables + scroll
		view.setMovementMethod(LinkMovementMethod.getInstance());

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private HelpText getHelpText() {
		InputStream stream = getResources().openRawResource(R.raw.help);
		MapperUtil<HelpText> mapperUtil = new MapperUtil<HelpText>(getObjectMapper(), HelpText.class);
		return mapperUtil.read(stream);
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

	private TextView getTextView() {
		return (TextView) findViewById(R.id.help_text_view);
	}

	private CustomApplication getCustomApplication() {
		return (CustomApplication) getApplicationContext();
	}
	
	private ObjectMapper getObjectMapper() {
		return getCustomApplication().getObjectMapper();
	}
}
