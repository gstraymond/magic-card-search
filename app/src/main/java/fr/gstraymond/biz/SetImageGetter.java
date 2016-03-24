package fr.gstraymond.biz;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;

import com.magic.card.search.commons.log.Log;

import java.io.IOException;

import fr.gstraymond.R;

public class SetImageGetter implements ImageGetter {

    private static final String PATH = "sets";
    private Context context;
    private Log log = new Log(this);

    public SetImageGetter(Context context) {
        this.context = context;
    }

    @Override
    public Drawable getDrawable(String assetPath) {
        try {
            Drawable drawable = Drawable.createFromStream(
                    getAssetManager().open(PATH + "/" + assetPath),
                    null);
            drawable.setBounds(0, 0, getWidth(drawable), getAssetSize());
            return drawable;
        } catch (IOException e) {
            log.e("getDrawable", e);
        }
        return null;
    }

    private AssetManager getAssetManager() {
        return context.getResources().getAssets();
    }

    private int getAssetSize() {
        return (int) context.getResources().getDimension(R.dimen.castingCostSize);
    }

    private int getWidth(Drawable drawable) {
        int height = getAssetSize();
        int dHeight = drawable.getIntrinsicHeight();
        int dWidth = drawable.getIntrinsicWidth();
        return dWidth * height / dHeight;
    }

}
