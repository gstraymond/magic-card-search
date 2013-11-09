package fr.gstraymond.android;

import java.util.ArrayList;
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

			List<String> publications = getCardPublications(card.getPublications());
			
			MagicCardViewPager viewPager = ((MagicCardViewPager) rootView.findViewById(R.id.pager))
					.setPublications(publications)
					.setPublicationTextView(publicationTextView);
			MagicCardPagerAdapter pagerAdapter = new MagicCardPagerAdapter(getFragmentManager())
				.setPublications(publications);
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
		String type = "<p>" + card.getType() + "</p>";
		String description = descriptionFormatter.format(card.getDescription());
		String publications = "";
		String html = title + frenchTitle + castingCost + PT + type + description + publications;

		CustomApplication applicationContext = (CustomApplication) getActivity().getApplicationContext();
		CastingCostAssetLoader castingCostAssetLoader = applicationContext.getCastingCostAssetLoader();
		return Html.fromHtml(html, new AssetLoader(castingCostAssetLoader), null);
	}
	
	private List<String> getCardPublications(List<Publication> publications) {
		final List<String> cardPictures = new ArrayList<String>();
		for (Publication publication : publications) {
			cardPictures.add(publication.getEdition() + "|" + publication.getImage());
		}
		return cardPictures;
	}
}
