package fr.gstraymond.ui.view.impl;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import fr.gstraymond.R;
import fr.gstraymond.biz.CastingCostImageGetter;
import fr.gstraymond.models.search.response.Card;
import fr.gstraymond.tools.CastingCostFormatter;
import fr.gstraymond.ui.view.CommonDisplayableView;

public class CastingCostView extends CommonDisplayableView<TextView> {

    private CastingCostFormatter ccFormatter;
    private Html.ImageGetter imageGetter;

    public CastingCostView(Context context) {
        this.ccFormatter = new CastingCostFormatter();
        this.imageGetter = CastingCostImageGetter.small(context);
    }

    @Override
    public void setValue(Card card, int position) {
        String castingCost = ccFormatter.format(card.getCastingCost());
        Spanned html = Html.fromHtml(castingCost, imageGetter, null);

        getView().setText(html);
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
