package fr.gstraymond.ui.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import fr.gstraymond.api.ui.view.DisplayableView;
import fr.gstraymond.db.json.JsonList;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.ui.CastingCostAssetLoader;
import fr.gstraymond.ui.view.impl.CastingCostView;
import fr.gstraymond.ui.view.impl.CostView;
import fr.gstraymond.ui.view.impl.DescriptionView;
import fr.gstraymond.ui.view.impl.FavoriteView;
import fr.gstraymond.ui.view.impl.TitleView;
import fr.gstraymond.ui.view.impl.TypePTView;

public class CardViews {
    private List<DisplayableView> displayableViews;

    public CardViews(Context context, CastingCostAssetLoader castingCostAssetLoader, JsonList wishlist, FavoriteView.ClickCallbacks clickCallbacks) {
        displayableViews = new ArrayList<>();
        displayableViews.add(new TitleView(context));
        displayableViews.add(new DescriptionView(castingCostAssetLoader));
        displayableViews.add(new CastingCostView(castingCostAssetLoader));
        displayableViews.add(new TypePTView(context));
        displayableViews.add(new FavoriteView(wishlist, clickCallbacks, context));
        displayableViews.add(new CostView());
    }

    public void display(View view, Card card, int position) {
        for (DisplayableView displayableView : displayableViews) {
            displayableView.setParentView(view);
            if (displayableView.display(card)) {
                displayableView.setValue(card, position);
            }
        }
    }
}
