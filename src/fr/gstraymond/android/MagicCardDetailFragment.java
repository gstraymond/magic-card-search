package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.MAGIC_CARD;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
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
	private DescriptionFormatter descriptionFormatter;

	public MagicCardDetailFragment() {
		this.castingCostFormatter = new CastingCostFormatter();
		this.descriptionFormatter = new DescriptionFormatter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MagicCard card = getArguments().getParcelable(MAGIC_CARD);
		View rootView = inflater.inflate(R.layout.fragment_magiccard_detail,
				container, false);

		TextView titleTextView = (TextView) rootView.findViewById(R.id.magiccard_detail_title);
		titleTextView.setText(formatTitle(card));
		
		TextView textView = (TextView) rootView.findViewById(R.id.magiccard_detail);
		textView.setText(formatCard(card));
		
		return rootView;
	}
	
	private Spanned formatCard(MagicCard card) {
		String castingCost = formatCC(card);
		String pt = formatPT(card);
		String type = formatType(card);
		String description = formatDescription(card);
		String html = getHtml(castingCost, pt, type, description);
		
		return Html.fromHtml(html, getImageGetter(), null);
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
	
	private ImageGetter getImageGetter() {
		return new CastingCostImageGetter(getAssetLoader());
	}

	private CastingCostAssetLoader getAssetLoader() {
		CustomApplication applicationContext = (CustomApplication) getActivity().getApplicationContext();
		return applicationContext.getCastingCostAssetLoader();
	}
	
	private String formatTitle(MagicCard card) {
		if (LanguageUtil.showFrench(getActivity()) && card.getFrenchTitle() != null) {
			return card.getFrenchTitle() + "\n(" + card.getTitle() + ")";
		}
		
		return card.getTitle();
	}
}
