package fr.gstraymond.ui.adapter;

import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.db.History;


public class HistoryArrayAdapter extends ArrayAdapter<History> {
	
	private java.text.DateFormat dateFormat;

	public HistoryArrayAdapter(Context context, int resource,
			int textViewResourceId, List<History> objects) {
		super(context, resource, textViewResourceId, objects);
		dateFormat = DateFormat.getLongDateFormat(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.array_adapter_history, null);
		}
		History history = getItem(position);

		TextView dateView = (TextView) view.findViewById(R.id.array_adapter_history_date);
		TextView queryView = (TextView) view.findViewById(R.id.array_adapter_history_query);

		dateView.setText(dateFormat.format(history.getDate()));
		queryView.setText(history.getQuery());
		
		return view;
	}
}
