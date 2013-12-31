package fr.gstraymond.biz;

import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class PictureDownloader extends AsyncTask<Void, Void, Bitmap> {

	private ImageView imageView;
	private String url;
	
	public PictureDownloader(ImageView imageView, String url) {
		this.imageView = imageView;
		this.url = url;
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		Log.d(getClass().getName(), "doInBackground : url " + url);
		return getBitmap();
	}
		
    @Override
    protected void onPostExecute(Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);
		Log.d(getClass().getName(), "height : " +  bitmap.getHeight() + " - width : " +  bitmap.getWidth());
    }

	private Bitmap getBitmap() {
        try {
            InputStream is = new URL(url).openStream();
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
        	Log.e(getClass().getName(), "error", e);
        }
		return null;
	}
}
