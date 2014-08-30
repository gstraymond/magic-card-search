package fr.gstraymond.ui.view.impl;

import android.view.View;
import android.widget.ImageButton;

import com.amazon.device.associates.AssociatesAPI;
import com.amazon.device.associates.LinkService;
import com.amazon.device.associates.NotInitializedException;
import com.amazon.device.associates.OpenSearchPageRequest;

import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.ui.view.CommonDisplayableView;

public class BuyButtonView extends CommonDisplayableView {

	@Override
	public boolean display(Card card) {
		return true;
	}

	@Override
	public int getId() {
		return 0; // R.id.array_adapter_buy_button;
	}

	@Override
	public void setValue(Card card, int position) {

		ImageButton button = (ImageButton) getView();
		button.setEnabled(true);
		final String searchTerm = "mtg " + card.getTitle();
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				OpenSearchPageRequest request = new OpenSearchPageRequest(
						searchTerm);
				try {
					LinkService linkService = AssociatesAPI.getLinkService();
					linkService.openRetailPage(request);
				} catch (NotInitializedException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
