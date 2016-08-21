package fr.gstraymond.ui.view.impl;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import fr.gstraymond.R;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.tools.LanguageUtil;
import fr.gstraymond.ui.view.CommonDisplayableView;

public class TitleView extends CommonDisplayableView<TextView> {
    private boolean showFrenchTitle;

    @Override
    public void setValue(Card card, int position) {
        getView().setText(formatCard(card));
    }

    public TitleView(Context context) {
        super();
        this.showFrenchTitle = LanguageUtil.showFrench(context);
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
