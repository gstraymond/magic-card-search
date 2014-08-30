package fr.gstraymond.ui.view.impl;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import fr.gstraymond.R;
import fr.gstraymond.biz.CastingCostImageGetter;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.tools.CastingCostFormatter;
import fr.gstraymond.ui.CastingCostAssetLoader;
import fr.gstraymond.ui.view.CommonDisplayableView;

public class CastingCostView extends CommonDisplayableView {
    private CastingCostImageGetter imageGetter;
    private CastingCostFormatter ccFormatter;

    public CastingCostView(CastingCostAssetLoader castingCostAssetLoader) {
        this.imageGetter = new CastingCostImageGetter(castingCostAssetLoader);
        this.ccFormatter = new CastingCostFormatter();
    }

    @Override
    public void setValue(Card card, int position) {
        String castingCost = ccFormatter.format(card.getCastingCost());
        Spanned html = Html.fromHtml(castingCost, imageGetter, null);

        TextView view = (TextView) getView();
        view.setText(html);
    }

    @Override
    public boolean display(Card card) {
        return super.display(!TextUtils.isEmpty(card.getCastingCost()));
    }

    @Override
    public int getId() {
        return R.id.array_adapter_card_casting_cost;
    }
}
