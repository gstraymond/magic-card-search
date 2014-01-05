package fr.gstraymond.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.biz.CastingCostImageGetter;
import fr.gstraymond.biz.SetImageGetter;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.magicsearch.model.response.Publication;
import fr.gstraymond.tools.CastingCostFormatter;
import fr.gstraymond.tools.DescriptionFormatter;


public class SetArrayAdapter extends ArrayAdapter<Object> {

	private CastingCostFormatter castingCostFormatter;
	private DescriptionFormatter descriptionFormatter;
	private SetImageGetter setImagetGetter;

	public SetArrayAdapter(Context context, int resource,
			int textViewResourceId, List<Object> objects) {
		super(context, resource, textViewResourceId, objects);
		this.setImagetGetter = new SetImageGetter(getContext());
		this.castingCostFormatter = new CastingCostFormatter();
		this.descriptionFormatter = new DescriptionFormatter();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Object object = getItem(position);
		TextView text = getTextView(parent, object);
		
		if(object instanceof MagicCard) {
			MagicCard card = (MagicCard) object;
			text.setText(formatCard(card));
		} else if (object instanceof Publication) {
			Publication publication = (Publication) object;
			text.setText(formatPublication(publication));
		}
		
		return text;
	}
	
	private Spanned formatCard(MagicCard card) {
		String castingCost = formatCC(card);
		String pt = formatPT(card);
		String type = formatType(card);
		String description = formatDescription(card);
		String html = getHtml(castingCost, pt, type, description);
		
		return Html.fromHtml(html, getCCImageGetter(), null);
	}
	
	private ImageGetter getCCImageGetter() {
		return new CastingCostImageGetter(getAssetLoader());
	}
	
	private CastingCostAssetLoader getAssetLoader() {
		CustomApplication applicationContext = (CustomApplication) getContext().getApplicationContext();
		return applicationContext.getCastingCostAssetLoader();
	}

	private String getHtml(String... strings) {
		StringBuilder builder = new StringBuilder();
		for (String string : strings) {
			if (!string.isEmpty() && !builder.toString().isEmpty()) {
				builder.append("<br /><br />");
			}
			builder.append(string);
		}
		return builder.toString();
	}

	private String formatCC(MagicCard card) {
		if (card.getCastingCost() == null) {
			return "";
		}
		return castingCostFormatter.format(card.getCastingCost());
	}

	private String formatPT(MagicCard card) {
		if (card.getPower() == null) {
			return "";
		}
		return card.getPower() + " / " + card.getToughness();
	}
	
	private String formatType(MagicCard card) {
		return card.getType().replaceAll("--", "—");
	}

	private String formatDescription(MagicCard card) {
		if (card.getDescription() == null) {
			return "";
		}
		return descriptionFormatter.format(card.getDescription());
	}

	private Spanned formatPublication(Publication publication) {
		String line = getEditionImage(publication) + " " + publication.getEdition();
		return Html.fromHtml(line, setImagetGetter, null);
	}

	private String getEditionImage(Publication pub) {
		if (pub.getStdEditionCode() == null) {
			return "";
		}
		
		return "<img src='" + pub.getStdEditionCode() + "/" + pub.getRarityCode() + ".png' />";
	}
	
	private TextView getTextView(ViewGroup parent, Object object) {
		if (object instanceof MagicCard) {
			return (TextView) getLayoutInflater().inflate(R.layout.card_textview, parent, false);
		}
		
		/* Need optimisation
		if (view != null) {
			return (TextView) view;
		}
		*/

		return (TextView) getLayoutInflater().inflate(R.layout.set_textview, parent, false);
	}
	
	private LayoutInflater getLayoutInflater() {
		Activity activity = (Activity) getContext();
		return activity.getLayoutInflater();
	}
}
