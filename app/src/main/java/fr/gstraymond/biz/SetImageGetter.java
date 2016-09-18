package fr.gstraymond.biz;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;

import com.magic.card.search.commons.log.Log;

import java.io.IOException;

import fr.gstraymond.search.model.response.Publication;

public class SetImageGetter {

    private static final String PATH = "sets";
    private Context context;
    private Log log = new Log(this);

    public SetImageGetter(Context context) {
        this.context = context;
    }

    public Drawable getDrawable(Publication pub) {
        if (pub.getStdEditionCode() == null) return null;

        String path = pub.getStdEditionCode() + "/" + pub.getRarityCode() + ".png";
        try {
            return Drawable.createFromStream(
                    getAssetManager().open(PATH + "/" + path),
                    null);
        } catch (IOException e) {
            log.e("getDrawable", e);
        }
        return null;
    }

    private AssetManager getAssetManager() {
        return context.getResources().getAssets();
    }

}
