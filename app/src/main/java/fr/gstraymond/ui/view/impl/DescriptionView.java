package fr.gstraymond.ui.view.impl;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import fr.gstraymond.R;
import fr.gstraymond.biz.CastingCostImageGetter;
import fr.gstraymond.models.response.Card;
import fr.gstraymond.tools.DescriptionFormatter;
import fr.gstraymond.ui.view.CommonDisplayableView;

public class DescriptionView extends CommonDisplayableView<TextView> {

    private DescriptionFormatter descFormatter;
    private Html.ImageGetter imageGetter;

    public DescriptionView(Context context) {
        this.descFormatter = new DescriptionFormatter();
        imageGetter = CastingCostImageGetter.small(context);
    }

    @Override
    public void setValue(Card card, int position) {
        String desc = descFormatter.format(card, false);
        Spanned html = Html.fromHtml(desc, imageGetter, null);

        getView().setText(html);
    }

    @Override
    public boolean display(Card card) {
        return super.display(!TextUtils.isEmpty(card.getDescription()));
    }

    @Override
    public int getId() {
        return R.id.array_adapter_description;
    }
}
