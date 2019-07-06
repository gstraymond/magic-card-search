package fr.gstraymond.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;

import java.security.MessageDigest;

import fr.gstraymond.models.search.response.Card;
import fr.gstraymond.tools.glide.RotateTransformation;

import static fr.gstraymond.tools.DimensionUtilsKt.dpToPx;

public class CardLoader {

    private String url;
    private Card card;
    private ImageView imageView;
    private RequestListener<Drawable> requestListener;

    public CardLoader(String url, Card card, ImageView imageView) {
        this.url = url;
        this.card = card;
        this.imageView = imageView;
    }

    public CardLoader(String url, Card card, ImageView imageView, RequestListener<Drawable> requestListener) {
        this.url = url;
        this.card = card;
        this.imageView = imageView;
        this.requestListener = requestListener;
    }

    public void load(Context context) {
        load(context, 1);
    }

    public void load(Context context, int factor) {
        RequestBuilder<Drawable> builder = Glide.with(context).load(url);
        if (requestListener != null) {
            builder = builder.listener(requestListener);
        }

        RoundedCorners roundedCorners = new RoundedCorners(dpToPx(context, 25 / factor));
        Crop crop = new Crop(0.75 / factor);

        if ("split".equals(card.getLayout())) {
            builder = builder.transform(
                    new RotateTransformation(90f),
                    crop,
                    roundedCorners
            );
        } else if ("flip".equals(card.getLayout()) && !card.getDescription().contains("flip")) {
            builder = builder.transform(
                    new RotateTransformation(180f),
                    crop,
                    roundedCorners
            );
        } else {
            builder = builder.transform(
                    crop,
                    roundedCorners
            );
        }

        builder.into(imageView);
    }

    class Crop extends BitmapTransformation {

        private double margin;

        Crop(double margin) {
            this.margin = margin;
        }

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool,
                                   @NonNull Bitmap toTransform,
                                   int outWidth,
                                   int outHeight) {
            int computedMargin = (int) (margin * outWidth / 100.0);
            return Bitmap.createBitmap(
                    toTransform,
                    computedMargin,
                    computedMargin,
                    toTransform.getWidth() - 2 * computedMargin,
                    toTransform.getHeight() - 2 * computedMargin
            );
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        }
    }
}
