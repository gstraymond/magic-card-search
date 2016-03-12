package fr.gstraymond.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import fr.gstraymond.R;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.biz.PictureDownloader;

public class CardFragment extends Fragment {

    public static final String URL = "url";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_card, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.fragment_card_picture);
        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_card_progress_bar);

        String url = getArguments().getString(URL).replace("http://", "https://");
        new PictureDownloader(imageView, progressBar, url, getCustomApplication()).execute();

        return rootView;
    }

    private CustomApplication getCustomApplication() {
        return (CustomApplication) getActivity().getApplication();
    }
}
