package fr.gstraymond.android.fragment;

import android.app.ListFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import fr.gstraymond.R;

public class CustomListFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setDivider(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.colorAccent)));
        getListView().setDividerHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
    }
}
