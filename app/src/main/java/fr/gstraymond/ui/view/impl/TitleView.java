package fr.gstraymond.ui.view.impl;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import fr.gstraymond.R;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.tools.LanguageUtil;
import fr.gstraymond.ui.view.CommonDisplayableView;

public class TitleView extends CommonDisplayableView<TextView> {
    private final boolean showFrenchTitle;
    private final Resources resources;

    @Override
    public void setValue(Card card, int position) {
        Integer color = android.R.color.white;
        if (card.getColors().contains("Gold")) color = R.color.gold;
        else if (card.getColors().contains("White")) color = R.color.white;
        else if (card.getColors().contains("Red")) color = R.color.red;
        else if (card.getColors().contains("Green")) color = R.color.green;
        else if (card.getColors().contains("Black")) color = R.color.black;
        else if (card.getColors().contains("Blue")) color = R.color.blue;
        else if (card.getColors().contains("Uncolored") && card.getType().contains("Artifact")) color = R.color.uncolored;

        getView().setText(formatCard(card));
        getView().setTextColor(ResourcesCompat.getColor(resources, color, null));
    }

    public TitleView(Context context) {
        super();
        this.showFrenchTitle = LanguageUtil.showFrench(context);
        this.resources = context.getResources();
    }

    @Override
    public boolean display(Card card) {
        return super.display(true);
    }

    @Override
    public int getId() {
        return R.id.array_adapter_text;
    }

    private Spanned formatCard(Card card) {
        return Html.fromHtml("<b>" + getTitle(card) + "</b>");
    }

    private String getTitle(Card card) {
        if (showFrenchTitle && card.getFrenchTitle() != null) {
            return card.getFrenchTitle();
        }
        return card.getTitle();
    }
}
