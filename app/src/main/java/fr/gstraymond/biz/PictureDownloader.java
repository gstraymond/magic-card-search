package fr.gstraymond.biz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.magic.card.search.commons.log.Log;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import fr.gstraymond.R;
import fr.gstraymond.android.CustomApplication;

public class PictureDownloader extends AsyncTask<Void, Void, Bitmap> {

    private ImageView imageView;
    private ProgressBar progressBar;
    private String url;
    private CustomApplication application;
    private Log log = new Log(this);

    public PictureDownloader(ImageView imageView, ProgressBar progressBar, String url, CustomApplication application) {
        super();
        this.imageView = imageView;
        this.progressBar = progressBar;
        this.url = url;
        this.application = application;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        log.d("doInBackground : url " + url);
        Bitmap bitmap = application.getBitmapCache().get(url);
        if (bitmap != null) {
            log.d("doInBackground : cache " + bitmap.getByteCount());
            return bitmap;
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(inputStream);
            log.d("doInBackground : download " + url);
            application.getBitmapCache().put(url, bitmap);
        } catch (FileNotFoundException e) {
            log.w("file not found: " + url);
            bitmap = BitmapFactory.decodeResource(application.getResources(), R.drawable.mtg_card_back);
        } catch (UnknownHostException e) {
            log.w("unknown host: " + e.getMessage());
        } catch (Exception e) {
            log.e("error", e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        progressBar.setVisibility(View.GONE);

        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ScaleType.FIT_XY);
        imageView.setAdjustViewBounds(true);
        imageView.setVisibility(View.VISIBLE);
    }
}
