package fr.gstraymond.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;

import fr.gstraymond.models.response.Card;
import fr.gstraymond.tools.glide.RotateTransformation;

public class CardLoader {

    private String url;
    private Card card;
    private ImageView imageView;
    private RequestListener<String, GlideDrawable> requestListener;

    public CardLoader(String url, Card card, ImageView imageView) {
        this.url = url;
        this.card = card;
        this.imageView = imageView;
    }

    public CardLoader(String url, Card card, ImageView imageView, RequestListener<String, GlideDrawable> requestListener) {
        this.url = url;
        this.card = card;
        this.imageView = imageView;
        this.requestListener = requestListener;
    }

    public void load(Context context) {
        DrawableRequestBuilder<String> builder = Glide.with(context).load(url);
        if (requestListener != null)
            builder = builder.listener(requestListener);

        if ("split".equals(card.getLayout())) {
            builder.bitmapTransform(
                    new RotateTransformation(context, 90f),
                    new RoundedCornersTransformation(context, 10, 2));
        } else if ("flip".equals(card.getLayout()) && !card.getDescription().contains("flip")) {
            builder.bitmapTransform(
                    new RotateTransformation(context, 180f),
                    new RoundedCornersTransformation(context, 10, 2));
        } else {
            builder.bitmapTransform(new RoundedCornersTransformation(context, 10, 2));
        }

        builder.into(imageView);
    }
}
