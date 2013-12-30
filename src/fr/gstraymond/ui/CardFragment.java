package fr.gstraymond.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import fr.gstraymond.biz.PictureDownloader;

public class CardFragment extends Fragment {

	private static final String URL = "url";
	private String url;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        ImageView imageView = new ImageView(getActivity());
        imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ScaleType.FIT_XY);
        imageView.setAdjustViewBounds(true);
        
        if (url == null) {
        	url = savedInstanceState.getString(URL);
    		Log.d(getClass().getName(), "onCreateView url restored " + url);
        }
        
        new PictureDownloader(imageView, url).execute();
        
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        layout.setGravity(Gravity.CENTER);
        layout.addView(imageView); 
        
        return layout;
    }

	public Fragment setCardUrl(String url) {
		this.url = url;
		return this;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(URL, url);
		Log.d(getClass().getName(), "onSaveInstanceState " + outState);
	}

}
