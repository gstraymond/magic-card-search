package fr.gstraymond.biz;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.widget.TextView;

import com.magic.card.search.commons.log.Log;

import java.io.InputStream;
import java.net.URL;

public class ImageGetterAsyncTask extends AsyncTask<Void, Void, BitmapDrawable> {

    private Log log = new Log(this);

    private LevelListDrawable drawable;
    private TextView textView;
    private String source;
    private Resources resources;

    public ImageGetterAsyncTask(TextView textView, LevelListDrawable drawable, String source) {
        this.textView = textView;
        this.drawable = drawable;
        this.source = source;
        this.resources = textView.getResources();
    }

    @Override
    protected BitmapDrawable doInBackground(Void... params) {
        log.d("doInBackground " + source);
        try {
            InputStream is = new URL(source).openStream();
            return new BitmapDrawable(resources, is);
        } catch (Exception e) {
            log.e("error", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(BitmapDrawable bitmapDrawable) {
        if (bitmapDrawable != null) {
            drawable.addLevel(1, 1, bitmapDrawable);
            drawable.setBounds(0, 0, calculateWidth(), calculateHeight(bitmapDrawable.getBitmap()));
            drawable.setLevel(1);

            // redraw view
            textView.setText(textView.getText());
        }
    }

    private int calculateWidth() {
        return changeSizeFactor(textView.getWidth());
    }

    private int calculateHeight(Bitmap bitmap) {
        int height = textView.getWidth() * bitmap.getHeight() / bitmap.getWidth();
        return changeSizeFactor(height);
    }

    private int changeSizeFactor(int size) {
        return (int) (size * 0.9);
    }
}