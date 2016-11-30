package fr.gstraymond.biz;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.text.Html;

import com.magic.card.search.commons.log.Log;

import java.io.IOException;

import fr.gstraymond.R;

public class CastingCostImageGetter implements Html.ImageGetter {

    private AssetManager assetManager;
    private int size;
    private Drawable drawable;

    private Log log = new Log(this);

    private CastingCostImageGetter(Context context, int sizeId) {
        this.size = (int) context.getResources().getDimension(sizeId);
        this.assetManager = context.getResources().getAssets();
    }

    public static Html.ImageGetter small(Context context) {
        return new CastingCostImageGetter(context, R.dimen.castingCostSize);
    }

    public static Html.ImageGetter large(Context context) {
        return new CastingCostImageGetter(context, R.dimen.largeCastingCostSize);
    }

    @Override
    public Drawable getDrawable(String url) {
        try {
            drawable = Drawable.createFromStream(assetManager.open("cc/hd/" + url), null);
            drawable.setBounds(0, 0, size, size);
        } catch (IOException e) {
            log.e("getDrawable: " + e.getMessage(), e);
            drawable = null;
        }
        return drawable;
    }
}
