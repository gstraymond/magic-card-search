package fr.gstraymond.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.magic.card.search.commons.log.Log;

import fr.gstraymond.R;
import fr.gstraymond.biz.PictureRequestListener;
import fr.gstraymond.glide.CardLoader;
import fr.gstraymond.glide.RoundedCornersTransformation;
import fr.gstraymond.models.search.response.Card;

public class CardFragment extends Fragment implements PictureRequestListener.Callbacks {

    public static final String URL = "url";
    public static final String CARD = "card";

    private Log log = new Log(this);

    private ProgressBar progressBar;
    private String url;
    private Card card;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);
        ImageView imageView = rootView.findViewById(R.id.fragment_card_picture);
        progressBar = rootView.findViewById(R.id.fragment_card_progress_bar);

        url = getArguments().getString(URL);
        card = getArguments().getParcelable(CARD);

        log.d("downloading %s...", url);
        if (url == null) {
            progressBar.setVisibility(View.GONE);
            Glide.with(getActivity()).fromResource()
                    .load(R.drawable.mtg_card_back)
                    .bitmapTransform(new RoundedCornersTransformation(getActivity(), dpToPx(20), dpToPx(2)))
                    .into(imageView);
        } else {
            new CardLoader(url, card, imageView, new PictureRequestListener(url, this)).load(getActivity());
        }

        return rootView;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void onDownloadComplete() {
        log.d("downloading %s complete (%s)", url, card.getTitle());
        progressBar.setVisibility(View.GONE);
    }
}
