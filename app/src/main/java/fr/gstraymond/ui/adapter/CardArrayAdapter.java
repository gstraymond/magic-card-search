package fr.gstraymond.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import fr.gstraymond.R;
import fr.gstraymond.api.ui.view.DisplayableView;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.ui.CastingCostAssetLoader;
import fr.gstraymond.ui.view.impl.CastingCostView;
import fr.gstraymond.ui.view.impl.DescriptionView;
import fr.gstraymond.ui.view.impl.PositionView;
import fr.gstraymond.ui.view.impl.TitleView;
import fr.gstraymond.ui.view.impl.TypePTView;

public class CardArrayAdapter extends ArrayAdapter<Card> {
	private List<DisplayableView> displayableViews;

	public CardArrayAdapter(Context context, int resource,
			int textViewResourceId, List<Card> objects,
			CastingCostAssetLoader castingCostAssetLoader) {
		super(context, resource, textViewResourceId, objects);

		displayableViews = new ArrayList<DisplayableView>();
		displayableViews.add(new TitleView(context));
//		displayableViews.add(new BuyButtonView());
		displayableViews.add(new DescriptionView(castingCostAssetLoader));
		displayableViews.add(new CastingCostView(castingCostAssetLoader));
		displayableViews.add(new TypePTView(context));
		displayableViews.add(new PositionView());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.array_adapter_card, null);
		}

		Card card = getItem(position);

		for (DisplayableView displayableView : displayableViews) {
			displayableView.setParentView(view);
			if (displayableView.display(card)) {
				displayableView.setValue(card, position);
			}
		}
		return view;
	}
}
