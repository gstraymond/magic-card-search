package fr.gstraymond.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.biz.SetImageGetter;
import fr.gstraymond.magicsearch.model.response.Publication;


public class SetArrayAdapter extends ArrayAdapter<Publication> {
	
	private SetImageGetter imagetGetter;

	public SetArrayAdapter(Context context, int resource,
			int textViewResourceId, List<Publication> objects) {
		super(context, resource, textViewResourceId, objects);
		this.imagetGetter = new SetImageGetter(getContext());
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		TextView text = getTextView(view, parent);
		Publication publication = getItem(position);
		text.setText(formatCard(publication, position));
		return text;

	}

	private Spanned formatCard(Publication publication, int position) {
		String line = getEditionImage(publication) + " " + publication.getEdition();
		return Html.fromHtml(line, imagetGetter, null);
	}

	private String getEditionImage(Publication pub) {
		if (pub.getStdEditionCode() == null) {
			return "";
		}
		
		return "<img src='" + pub.getStdEditionCode() + "/" + pub.getRarityCode() + ".png' />";
	}
	
	private TextView getTextView(View view, ViewGroup parent) {
		if (view != null) {
			return (TextView) view;
		}

		return (TextView) getLayoutInflater().inflate(R.layout.set_textview, parent, false);
	}
	
	private LayoutInflater getLayoutInflater() {
		Activity activity = (Activity) getContext();
		return activity.getLayoutInflater();
	}
}
