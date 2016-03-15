package fr.gstraymond.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fr.gstraymond.R;
import fr.gstraymond.tools.Log;

public class CastingCostAssetLoader {

    private static final String PATH = "cc/hd";

    private Context context;
    private Map<String, Drawable> assets;
    private boolean init = false;
    private Log log = new Log(this);

    public void init(Context context) {
        log.d("init");
        if (!init) {
            init = true;
            this.context = context;
            this.assets = new HashMap<>();

            try {
                for (String assetPath : context.getAssets().list(PATH)) {
                    Drawable drawable = getDrawable(PATH + "/" + assetPath);
                    drawable.setBounds(0, 0, getAssetSize(), getAssetSize());
                    assets.put(assetPath, drawable);
                }
            } catch (IOException e) {
                log.e("init", e);
            }
        }
    }

    private Drawable getDrawable(String assetPath) throws IOException {
        return Drawable.createFromStream(getAssetManager().open(assetPath),
                null);
    }

    public Drawable get(String file) {
        return assets.get(file);
    }

    private AssetManager getAssetManager() {
        return context.getResources().getAssets();
    }

    private int getAssetSize() {
        return (int) context.getResources().getDimension(R.dimen.castingCostSize);
    }
}
