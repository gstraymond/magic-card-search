package fr.gstraymond.ui.view;

import android.view.View;

import fr.gstraymond.api.ui.view.DisplayableView;

public abstract class CommonDisplayableView<A extends View> implements DisplayableView {

    private View parentView;

    @Override
    public void setParentView(View parentView) {
        this.parentView = parentView;
    }

    @Override
    public A getView() {
        return (A) parentView.findViewById(getId());
    }

    private boolean show() {
        getView().setVisibility(View.VISIBLE);
        return true;
    }

    private boolean hide() {
        getView().setVisibility(View.GONE);
        return false;
    }

    public boolean display(boolean display) {
        return display ? show() : hide();
    }
}
