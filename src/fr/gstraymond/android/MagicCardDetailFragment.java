package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.MAGIC_CARD;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.biz.CastingCostImageGetter;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.tools.CastingCostFormatter;
import fr.gstraymond.tools.DescriptionFormatter;
import fr.gstraymond.tools.LanguageUtil;
import fr.gstraymond.ui.CastingCostAssetLoader;

public class MagicCardDetailFragment extends Fragment {

	private CastingCostFormatter castingCostFormatter;

	public MagicCardDetailFragment() {
		this.castingCostFormatter = new CastingCostFormatter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MagicCard card = getArguments().getParcelable(MAGIC_CARD);
		View rootView = inflater.inflate(R.layout.fragment_magiccard_detail,
				container, false);

		TextView textView = (TextView) rootView.findViewById(R.id.magiccard_detail);
		textView.setText(formatCard(card, textView));
		return rootView;
	}
	
	private Spanned formatCard(MagicCard card, final TextView textView) {
		DescriptionFormatter descriptionFormatter = new DescriptionFormatter();
		
		String castingCost = card.getCastingCost() != null ? "<p>" + castingCostFormatter.format(card.getCastingCost()) + "</p>" : "";
		String PT = card.getPower() != null ? "<p>" + card.getPower() + " / " + card.getToughness() + "</p>" : "";
		String type = "<p>" + formatType(card) + "</p>";
		String description = descriptionFormatter.format(card.getDescription());
		String html = formatTitle(card) + castingCost + PT + type + description;

		CustomApplication applicationContext = (CustomApplication) getActivity().getApplicationContext();
		CastingCostAssetLoader castingCostAssetLoader = applicationContext.getCastingCostAssetLoader();
		return Html.fromHtml(html, new CastingCostImageGetter(castingCostAssetLoader), null);
	}
	
	private String formatTitle(MagicCard card) {
		if (LanguageUtil.showFrench(getActivity()) && card.getFrenchTitle() != null) {
			return "<p>" + card.getFrenchTitle() + "<br/>(" + card.getTitle() + ")</p>";
		}
		
		return "<p>" + card.getTitle() + "</p>";
	}
	
	private String formatType(MagicCard card) {
		return card.getType().replaceAll("--", "—");
	}
}
