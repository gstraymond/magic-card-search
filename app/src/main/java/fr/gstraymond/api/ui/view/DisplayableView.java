package fr.gstraymond.api.ui.view;

import android.view.View;

import fr.gstraymond.models.search.response.Card;

public interface DisplayableView {

    boolean display(Card card);

    View getView();

    int getId();

    void setValue(Card card, int position);

    void setParentView(View parentView);
}
