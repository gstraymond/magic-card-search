package fr.gstraymond.ui.view.impl;

import android.widget.TextView;

import fr.gstraymond.R;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.ui.view.CommonDisplayableView;

public class PositionView extends CommonDisplayableView<TextView> {

    @Override
    public boolean display(Card card) {
        return super.display(true);
    }

    @Override
    public int getId() {
        return R.id.array_adapter_card_position;
    }

    @Override
    public void setValue(Card card, int position) {
        getView().setText(String.format("%d", position + 1));
    }

}
