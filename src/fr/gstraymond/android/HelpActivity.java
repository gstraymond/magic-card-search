package fr.gstraymond.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gstraymond.R;
import fr.gstraymond.biz.CastingCostImageGetter;
import fr.gstraymond.magicsearch.help.HelpText;
import fr.gstraymond.tools.LanguageUtil;

public class HelpActivity extends Activity {

	public static final String LANGUAGE = "language";
	public static final String EN = "en";
	public static final String FR = "fr";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		CastingCostImageGetter assetLoader = new CastingCostImageGetter(getCustomApplication().getCastingCostAssetLoader());

		String language = LanguageUtil.showFrench(this) ? FR : EN;
		HelpText helpText = getHelpText(language);
		Spanned text = format(helpText, assetLoader);

		TextView view = getTextView();
		view.setText(text);
		// rends les liens cliquables + scroll
		view.setMovementMethod(LinkMovementMethod.getInstance());

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private HelpText getHelpText(String language) {
		InputStream inputStream = getJsonFile(language);
		return parse(inputStream);
	}

	private InputStream getJsonFile(String language) {
		try {
			return getAssets().open("json/help_" + language + ".json");
		} catch (IOException e) {
			Log.e(getClass().getName(), "Error", e);
		}
		return null;
	}

	private HelpText parse(InputStream inputStream) {
		ObjectMapper objectMapper = getObjectMapper();
		try {
			return objectMapper.readValue(inputStream, HelpText.class);
		} catch (JsonParseException e) {
			Log.e(getClass().getName(), "Error", e);
		} catch (JsonMappingException e) {
			Log.e(getClass().getName(), "Error", e);
		} catch (IOException e) {
			Log.e(getClass().getName(), "Error", e);
		}
		return null;
	}

	private Spanned format(HelpText helpText, CastingCostImageGetter assetLoader) {
		StringBuilder html = new StringBuilder();
		recursiveFormat(html, helpText, 1, "");
		return Html.fromHtml(html.toString(), assetLoader, null);
	}

	private void recursiveFormat(StringBuilder html, HelpText helpText,
			int level, String depth) {
		html.append(formatTitle(helpText, level, depth));
		html.append(formatDescription(helpText));
		html.append(formatItems(helpText));

		if (helpText.getTexts() != null) {
			int i = 1;
			for (HelpText subHelpText : helpText.getTexts()) {
				recursiveFormat(html, subHelpText, level + 1, depth + i + "." );
				i++;
			}
		}
	}

	private String formatTitle(HelpText helpText, int level, String depth) {
		String title = helpText.getTitle();
		if (title == null) {
			return "";
		}
		String sign = "h" + level + ">";
		StringBuilder levels = new StringBuilder();
		for (int i = 0; i < level - 1; i++) {
			levels.append("\t");
		}
		return "<" + sign + depth + " " + title + "</" + sign;
	}

	private String formatDescription(HelpText helpText) {
		List<String> descriptions = helpText.getDescriptions();
		if (descriptions == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder("<p>");
		for (String description : descriptions) {
			builder.append(description + "<br />");
		}
		return builder.toString() + "</p>";
	}

	private String formatItems(HelpText helpText) {
		List<String> items = helpText.getItems();
		if (items == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder("<p>");
		for (String item : items) {
			builder.append("\t\t●\t" + item + "<br />");
		}
		return builder.toString() + "</p>";
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
