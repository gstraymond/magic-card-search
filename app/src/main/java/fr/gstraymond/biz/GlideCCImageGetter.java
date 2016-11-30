package fr.gstraymond.biz;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.magic.card.search.commons.log.Log;

import java.util.HashSet;
import java.util.Set;

import fr.gstraymond.R;

public class GlideCCImageGetter implements Html.ImageGetter, Drawable.Callback {

    private Context context;
    private TextView textView;
    private Set<ImageGetterViewTarget> targets;
    private int size;

    private Log log = new Log(this);

    public static GlideCCImageGetter get(View view) {
        return (GlideCCImageGetter) view.getTag(R.id.card_alt); // FIXME
    }

    private void clear() {
        GlideCCImageGetter prev = get(textView);
        if (prev == null) return;

        for (ImageGetterViewTarget target : prev.targets) {
            Glide.clear(target);
        }
    }

    public GlideCCImageGetter(Context context, TextView textView, int sizeId) {
        this.context = context;
        this.textView = textView;
        this.size = (int) context.getResources().getDimension(sizeId); //  R.dimen.castingCostSize

        clear();
        targets = new HashSet<>();
        this.textView.setTag(R.id.card_alt, this); // FIXME
    }

    @Override
    public Drawable getDrawable(String url) {
        UrlDrawable urlDrawable = new UrlDrawable();

        String newUrl = "file:///android_asset/cc/hd/" + url;

        log.d("Downloading from: %s", newUrl);
        Glide.with(context)
                .load(newUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new ImageGetterViewTarget(textView, urlDrawable));

        return urlDrawable;

    }

    @Override
    public void invalidateDrawable(Drawable who) {
        textView.invalidate();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {

    }

    private class ImageGetterViewTarget extends ViewTarget<TextView, GlideDrawable> {

        private UrlDrawable drawable;

        private ImageGetterViewTarget(TextView view, UrlDrawable drawable) {
            super(view);
            targets.add(this);
            this.drawable = drawable;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            Rect rect = new Rect(0, 0, size, size);
            resource.setBounds(rect);

            drawable.setBounds(rect);
            drawable.setDrawable(resource);

            getView().setText(getView().getText());
            getView().invalidate();
        }

        private Request request;

        @Override
        public Request getRequest() {
            return request;
        }

        @Override
        public void setRequest(Request request) {
            this.request = request;
        }
    }
}
