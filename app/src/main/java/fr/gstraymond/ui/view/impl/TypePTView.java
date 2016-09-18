package fr.gstraymond.ui.view.impl;

import android.content.Context;
import android.widget.TextView;

import fr.gstraymond.R;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.tools.PowerToughnessFormatter;
import fr.gstraymond.tools.TypeFormatter;
import fr.gstraymond.ui.view.CommonDisplayableView;

public class TypePTView extends CommonDisplayableView<TextView> {

    private PowerToughnessFormatter ptFormatter;
    private TypeFormatter typeFormatter;

    public TypePTView(Context context) {
        this.ptFormatter = new PowerToughnessFormatter();
        this.typeFormatter = new TypeFormatter(context);
    }

    @Override
    public void setValue(Card card, int position) {
        String ptOrType = getPtOrType(card);
        if (ptOrType.isEmpty()) getView().setText("");
        else getView().setText(" — " + ptOrType);
    }

    @Override
    public boolean display(Card card) {
        return super.display(true);
    }

    @Override
    public int getId() {
        return R.id.array_adapter_card_type_pt;
    }

    private String getPtOrType(Card card) {
        String pt = ptFormatter.format(card);
        if (!pt.isEmpty()) return pt;

        return typeFormatter.formatFirst(card);
    }
}
