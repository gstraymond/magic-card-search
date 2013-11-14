package fr.gstraymond.android;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.biz.AssetLoader;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.magicsearch.model.response.Publication;
import fr.gstraymond.tools.CastingCostFormatter;
import fr.gstraymond.tools.DescriptionFormatter;
import fr.gstraymond.ui.CastingCostAssetLoader;
import fr.gstraymond.ui.MagicCardPagerAdapter;
import fr.gstraymond.ui.MagicCardViewPager;

public class MagicCardDetailFragment extends Fragment {

	public static final String MAGIC_CARD = "magic_card";

	private MagicCard card;
	private CastingCostFormatter castingCostFormatter;

	public MagicCardDetailFragment() {
		this.castingCostFormatter = new CastingCostFormatter();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(MAGIC_CARD)) {
			card = getArguments().getParcelable(MAGIC_CARD);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		boolean attachToRoot = false;
		View rootView = inflater.inflate(R.layout.fragment_magiccard_detail,
				container, attachToRoot);

		if (card != null) {
			TextView publicationTextView = (TextView) rootView.findViewById(R.id.publication);
			TextView textView = (TextView) rootView.findViewById(R.id.magiccard_detail);
			textView.setText(formatCard(card, textView));
			
			MagicCardViewPager viewPager = ((MagicCardViewPager) rootView.findViewById(R.id.pager))
					.setPublications(card.getPublications())
					.setPublicationTextView(publicationTextView);
			MagicCardPagerAdapter pagerAdapter = new MagicCardPagerAdapter(getFragmentManager())
				.setPublications(card.getPublications());
			viewPager.setAdapter(pagerAdapter);
		}

		return rootView;
	}
	
	private Spanned formatCard(MagicCard card, final TextView textView) {
		DescriptionFormatter descriptionFormatter = new DescriptionFormatter();
		
		String title = "<p>" + card.getTitle() + "</p>";
		String frenchTitle = card.getFrenchTitle() != null ? "<p>" + card.getFrenchTitle() + "</p>" : "";
		String castingCost = card.getCastingCost() != null ? "<p>" + castingCostFormatter.format(card.getCastingCost()) + "</p>" : "";
		String PT = card.getPower() != null ? "<p>" + card.getPower() + " / " + card.getToughness() + "</p>" : "";
		String type = "<p>" + formatType(card) + "</p>";
		String description = descriptionFormatter.format(card.getDescription());
		String publications = getCardPublications(card.getPublications());
		String html = title + frenchTitle + castingCost + PT + type + description + publications;

		CustomApplication applicationContext = (CustomApplication) getActivity().getApplicationContext();
		CastingCostAssetLoader castingCostAssetLoader = applicationContext.getCastingCostAssetLoader();
		return Html.fromHtml(html, new AssetLoader(castingCostAssetLoader), null);
	}
	
	private String formatType(MagicCard card) {
		return card.getType().replaceAll("--", "—");
	}
	
	private String getCardPublications(List<Publication> publications) {
		final StringBuilder html = new StringBuilder("<p>");
		for (Publication publication : publications) {
			html.append(" • ");
			html.append(publication.getEdition());
			html.append(" (");
			html.append(publication.getRarity());
			html.append(")");
			html.append("<br />");
		}
		return html.append("</p>").toString();
	}
}
