package fr.gstraymond.android.fragment;

import android.os.Bundle;
import androidx.fragment.app.ListFragment;

public class CustomListFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setDividerHeight(0);
    }
}
