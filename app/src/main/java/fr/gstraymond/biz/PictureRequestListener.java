package fr.gstraymond.biz;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magic.card.search.commons.log.Log;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class PictureRequestListener implements RequestListener<String, GlideDrawable> {

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
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        if (e instanceof IOException) {
            log.w("error downloading: %s %s", url, e.getMessage());
        } else  {
            log.e("error downloading: " + url, e);
        }
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        callbacks.onDownloadComplete();
        return false;
    }
}
