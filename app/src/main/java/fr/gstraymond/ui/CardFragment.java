package fr.gstraymond.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.magic.card.search.commons.log.Log;

import fr.gstraymond.R;
import fr.gstraymond.biz.PictureRequestListener;
import fr.gstraymond.glide.RoundedCornersTransformation;
import fr.gstraymond.tools.glide.RotateTransformation;

public class CardFragment extends Fragment implements PictureRequestListener.Callbacks {

    public static final String URL = "url";
    public static final String ROTATE = "rotate";

    private Log log = new Log(this);

    private ProgressBar progressBar;
    private String url;
    private Boolean rotate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.fragment_card_picture);
        progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_card_progress_bar);

        url = getArguments().getString(URL);
        rotate = getArguments().getBoolean(ROTATE);

        log.d("downloading %s...", url);
        RequestManager request = Glide.with(getActivity());
        DrawableRequestBuilder<?> builder;
        if (url == null) {
            progressBar.setVisibility(View.GONE);
            builder = request.fromResource()
                    .load(R.drawable.mtg_card_back)
                    .bitmapTransform(new RoundedCornersTransformation(getActivity(), dpToPx(20), dpToPx(2)));
        } else {
            builder = request.load(url).listener(new PictureRequestListener(url, this));

            if (rotate)
                builder.bitmapTransform(
                        new RotateTransformation(getActivity(), 90f),
                        new RoundedCornersTransformation(getActivity(), 10, 2));
            else
                builder.bitmapTransform(new RoundedCornersTransformation(getActivity(), 10, 2));

        }
        builder.into(imageView);

        return rootView;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void onDownloadComplete() {
        log.d("downloading %s complete (rotate? %s)", url, rotate);
        progressBar.setVisibility(View.GONE);
    }
}
