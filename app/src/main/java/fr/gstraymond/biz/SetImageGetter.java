package fr.gstraymond.biz;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;

import com.magic.card.search.commons.log.Log;

import java.io.IOException;

import fr.gstraymond.models.response.Publication;

public class SetImageGetter {

    private static final String PATH = "sets";
    private Context context;
    private Log log = new Log(this);

    public SetImageGetter(Context context) {
        this.context = context;
    }

    public Drawable getDrawable(Publication pub) {
        String stdEditionCode = pub.getStdEditionCode();
        if (stdEditionCode == null) return null;
        return getDrawable(stdEditionCode, pub.getRarityCode());
    }

    private Drawable getDrawable(String stdEditionCode, String rarityCode) {
        String path = stdEditionCode + "/" + rarityCode + ".png";
        try {
            return Drawable.createFromStream(
                    getAssetManager().open(PATH + "/" + path),
                    null);
        } catch (IOException e) {
            log.w("getDrawable %s", e.getMessage());
        }
        return null;
    }

    public Drawable getDrawable(String stdEditionCode) {
        if (stdEditionCode == null) return null;

        Drawable drawable = getDrawable(stdEditionCode, "M");
        if (drawable == null) drawable = getDrawable(stdEditionCode, "R");
        if (drawable == null) drawable = getDrawable(stdEditionCode, "S");
        if (drawable == null) drawable = getDrawable(stdEditionCode, "U");
        if (drawable == null) drawable = getDrawable(stdEditionCode, "C");
        return drawable;
    }

    private AssetManager getAssetManager() {
        return context.getResources().getAssets();
    }

}
