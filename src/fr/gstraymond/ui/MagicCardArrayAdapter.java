package fr.gstraymond.ui;

import static android.R.style.TextAppearance_DeviceDefault_Medium;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.biz.AssetLoader;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.tools.CastingCostFormatter;

public class MagicCardArrayAdapter extends ArrayAdapter<MagicCard> {

	private AssetLoader assetLoader;
	private CastingCostFormatter castingCostFormatter;

	public MagicCardArrayAdapter(Context context, int resource,
			int textViewResourceId, List<MagicCard> objects,
			CastingCostAssetLoader castingCostAssetLoader) {
		
		super(context, resource, textViewResourceId, objects);
		this.assetLoader = new AssetLoader(castingCostAssetLoader);
		this.castingCostFormatter = new CastingCostFormatter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = (TextView) convertView;

		if (text == null) {
			text = new TextView(getContext());
			text.setTextAppearance(getContext(), TextAppearance_DeviceDefault_Medium);
			text.setPadding(getTextPaddingBottom() * 2, 0, 0, getTextPaddingBottom());
		}

		text.setText(formatCard(getItem(position), position));
		return text;

	}

	private int getTextPaddingBottom() {
		return (int) getContext().getResources().getDimension(R.dimen.listTextPaddingBottom);
	}	

	private Spanned formatCard(MagicCard card, int position) {

		String cc = "";
		if (card.getCastingCost() != null) {
			cc = castingCostFormatter.format(card.getCastingCost());
		}

		String line = (position + 1) + ". " + cc + " " + card.getTitle();

		return Html.fromHtml(line, assetLoader, null);
	}
}
