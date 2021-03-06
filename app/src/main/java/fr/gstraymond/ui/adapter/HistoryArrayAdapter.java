package fr.gstraymond.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.gstraymond.R;
import fr.gstraymond.constants.FacetConst;
import fr.gstraymond.models.History;
import fr.gstraymond.db.json.HistoryList;


public class HistoryArrayAdapter extends ArrayAdapter<History> {

    private java.text.DateFormat dayFormat;
    private java.text.DateFormat dateFormat;
    private java.text.DateFormat timeFormat;
    private View.OnClickListener clickListener;

    public HistoryArrayAdapter(final Context context, int resource,
                               int textViewResourceId, List<History> objects,
                               final HistoryList historyList) {
        super(context, resource, textViewResourceId, objects);
        dateFormat = DateFormat.getDateFormat(context);
        timeFormat = DateFormat.getTimeFormat(context);
        dayFormat = new SimpleDateFormat("MMddyyyy", context.getResources().getConfiguration().locale);

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkbox = (CheckBox) view;
                History history = (History) checkbox.getTag();
                historyList.manageFavorite(history, checkbox.isChecked());
            }
        };
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.array_adapter_history, null);
        }
        History history = getItem(position);

        CheckBox favoriteView = (CheckBox) view.findViewById(R.id.array_adapter_history_favorite);
        TextView indexView = (TextView) view.findViewById(R.id.array_adapter_history_index);
        TextView dateView = (TextView) view.findViewById(R.id.array_adapter_history_date);
        TextView queryView = (TextView) view.findViewById(R.id.array_adapter_history_query);

        indexView.setText((position + 1) + ".");
        dateView.setText(formatDate(history));
        queryView.setText(formatQueryFacets(history));
        favoriteView.setChecked(history.isFavorite());
        favoriteView.setOnClickListener(clickListener);
        favoriteView.setTag(history);

        return view;
    }

    private String formatDate(History history) {
        Date historyDate = history.getDate();

        String hDate = dayFormat.format(historyDate);
        String nDate = dayFormat.format(new Date());

        if (nDate.equals(hDate)) {
            return timeFormat.format(historyDate);
        }
        return dateFormat.format(historyDate);
    }

    private Spanned formatQueryFacets(History history) {
        if (history.getQuery().equals("*")) {
            return Html.fromHtml(formatFacets(history));
        }
        String query = getContext().getString(R.string.history_search_text) + ": <b>" + history.getQuery() + "</b>";
        return Html.fromHtml(query + "<br />" + formatFacets(history));
    }

    private String formatFacets(History history) {
        Map<String, List<String>> facets = history.getFacets();
        if (facets.isEmpty()) {
            return "";
        }

        List<String> l = new ArrayList<>();
        for (Map.Entry<String, List<String>> e : facets.entrySet()) {
            String facetName = FacetConst.getFacetName(e.getKey(), getContext());
            if (facetName != null) {
                l.add(facetName + ": <b>" + TextUtils.join("</b>, <b>", e.getValue()) + "</b>");
            }
        }
        return TextUtils.join("<br/>", l);
    }
}
