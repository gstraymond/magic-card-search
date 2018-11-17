package fr.gstraymond.android.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class CustomListFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setDividerHeight(0);
    }
}
