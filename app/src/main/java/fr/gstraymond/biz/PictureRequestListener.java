package fr.gstraymond.biz;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magic.card.search.commons.log.Log;

public class PictureRequestListener implements RequestListener<Drawable> {

    public interface Callbacks {
        void onDownloadComplete();
    }

    private Log log = new Log(this);

    private String url;
    private Callbacks callbacks;

    public PictureRequestListener(String url, Callbacks callbacks) {
        this.url = url;
        this.callbacks = callbacks;
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e,
                                Object model,
                                Target<Drawable> target,
                                boolean isFirstResource) {
        if (e != null) {
            log.w("error downloading: %s %s", url, e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource,
                                   Object model,
                                   Target<Drawable> target,
                                   DataSource dataSource,
                                   boolean isFirstResource) {
        callbacks.onDownloadComplete();
        return false;
    }
}
