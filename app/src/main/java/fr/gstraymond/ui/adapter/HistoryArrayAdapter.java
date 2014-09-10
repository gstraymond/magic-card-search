package fr.gstraymond.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.gstraymond.R;
import fr.gstraymond.biz.Facets;
import fr.gstraymond.constants.FacetConst;
import fr.gstraymond.db.History;


public class HistoryArrayAdapter extends ArrayAdapter<History> {

    private java.text.DateFormat dateFormat;
    private java.text.DateFormat timeFormat;

    public HistoryArrayAdapter(Context context, int resource,
                               int textViewResourceId, List<History> objects) {
        super(context, resource, textViewResourceId, objects);
        dateFormat = DateFormat.getLongDateFormat(context);
        timeFormat = DateFormat.getTimeFormat(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.array_adapter_history, null);
        }
        History history = getItem(position);

        //CheckBox favoriteView = (CheckBox) view.findViewById(R.id.array_adapter_history_favorite);
        TextView dateView = (TextView) view.findViewById(R.id.array_adapter_history_date);
        TextView queryView = (TextView) view.findViewById(R.id.array_adapter_history_query);
        TextView facetsView = (TextView) view.findViewById(R.id.array_adapter_history_facets);

        Date historyDate = history.getDate();
        dateView.setText(history.getId() + ". " + /*dateFormat.format(historyDate) + " " + */timeFormat.format(historyDate));
        queryView.setText(history.getQuery());
        //favoriteView.setChecked(history.isFavorite());
        facetsView.setText(formatFacets(history));

        return view;
    }

    private String formatFacets(History history) {
        Facets facets = history.getFacets();
        if (facets.isEmpty()) {
            return "";
        }

        List<String> l = new ArrayList<String>();
        for (Map.Entry<String, List<String>> e : facets.entrySet()) {
            String facetName = FacetConst.getFacetName(e.getKey(), getContext());
            l.add(facetName + ": " + TextUtils.join(", ", e.getValue()));
        }
        return TextUtils.join("\n", l);
    }
}
