package fr.gstraymond.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.magic.card.search.commons.log.Log;

import fr.gstraymond.R;
import fr.gstraymond.biz.PictureRequestListener;

public class CardFragment extends Fragment implements PictureRequestListener.Callbacks {

    public static final String URL = "url";

    private Log log = new Log(this);

    private ImageView imageView;
    private ProgressBar progressBar;
    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.fragment_card_picture);
        progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_card_progress_bar);

        url = getArguments().getString(URL);

        log.d("downloading %s...", url);
        if (url == null) {
            progressBar.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.mtg_card_back);
        } else {
            Glide.with(getActivity())
                    .load(url)
                    .listener(new PictureRequestListener(url, this))
                    .into(imageView);
        }

        return rootView;
    }

    @Override
    public void onDownloadComplete() {
        log.d("downloading %s complete", url);
        progressBar.setVisibility(View.GONE);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setAdjustViewBounds(true);
    }
}
