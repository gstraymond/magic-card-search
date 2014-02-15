package fr.gstraymond.biz;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PictureDownloader extends AsyncTask<Void, Void, Bitmap> {

	private ImageView imageView;
	private String url;
	private Map<String, Bitmap> cardBitmapCache; 
	
	public PictureDownloader(ImageView imageView, String url, Map<String, Bitmap> cache) {
		this.imageView = imageView;
		this.url = url;
		this.cardBitmapCache = cache;
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		Log.d(getClass().getName(), "doInBackground : url " + url);
		return getBitmap();
	}
		
    @Override
    protected void onPostExecute(Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ScaleType.FIT_XY);
        imageView.setAdjustViewBounds(true);
    }

	private Bitmap getBitmap() {
		if (cardBitmapCache.containsKey(url)) {
			return cardBitmapCache.get(url);
		}
		
        try {
            InputStream is = new URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            cardBitmapCache.put(url, bitmap);
			return bitmap;
        } catch (Exception e) {
        	Log.e(getClass().getName(), "error", e);
        }
		return null;
	}
}
