package fr.gstraymond.ui.view.impl;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.biz.CastingCostImageGetter;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.tools.DescriptionFormatter;
import fr.gstraymond.ui.CastingCostAssetLoader;
import fr.gstraymond.ui.view.CommonDisplayableView;

public class DescriptionView extends CommonDisplayableView {

	private CastingCostImageGetter imageGetter;
	private DescriptionFormatter descFormatter;

	public DescriptionView(CastingCostAssetLoader castingCostAssetLoader) {
		this.imageGetter = new CastingCostImageGetter(castingCostAssetLoader);
		descFormatter = new DescriptionFormatter();
	}

	@Override
	public void setValue(Card card, int position) {
		String desc = descFormatter.format(card);
		Spanned html = Html.fromHtml(desc, imageGetter, null);
		
		TextView view = (TextView) getView();
		view.setText(html);
	}

	@Override
	public boolean display(Card card) {
		return super.display(! TextUtils.isEmpty(card.getDescription()));
	}

	@Override
	public int getId() {
		return R.id.array_adapter_description;
	}
}
