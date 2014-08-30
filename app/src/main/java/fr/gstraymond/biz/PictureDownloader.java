package fr.gstraymond.biz;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import fr.gstraymond.android.CustomApplication;

public class PictureDownloader extends AsyncTask<Void, Void, Bitmap> {

	private ImageView imageView;
	private ProgressBar progressBar;
	private String url;
	private CustomApplication application;
	
	public PictureDownloader(ImageView imageView, ProgressBar progressBar, String url, CustomApplication application) {
		super();
		this.imageView = imageView;
		this.progressBar = progressBar;
		this.url = url;
		this.application = application;
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		Log.d(getClass().getName(), "doInBackground : url " + url);
		return getBitmap();
	}
		
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		progressBar.setVisibility(View.GONE);

		imageView.setImageBitmap(bitmap);
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setAdjustViewBounds(true);
		imageView.setVisibility(View.VISIBLE);
	}

	private Bitmap getBitmap() {
		Bitmap bitmap = application.getBitmapCache().get(url);
		if (bitmap != null) {
			return bitmap;
		}
		
        try {
    		HttpGet getRequest = new HttpGet(url);
			HttpResponse response = application.getHttpClient().execute(getRequest);
    		InputStream content = response.getEntity().getContent();
    		bitmap = BitmapFactory.decodeStream(content);
    		application.getBitmapCache().put(url, bitmap);
			return bitmap;
        } catch (Exception e) {
        	Log.e(getClass().getName(), "error", e);
        }
		return null;
	}
}
