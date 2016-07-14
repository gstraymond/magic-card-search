package fr.gstraymond.android.fragment;

import android.app.ListFragment;
import android.os.Bundle;

public class CustomListFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setDividerHeight(0);
    }
}
