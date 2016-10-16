package fr.gstraymond.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.gstraymond.R;
import fr.gstraymond.autocomplete.response.Option;
import fr.gstraymond.biz.SetImageGetter;


public class SearchViewCursorAdapter extends CursorAdapter {

    private static String FIELD_TYPE = "type";
    private static String FIELD_TEXT = "text";
    private static String FIELD_EDITION_CODE = "editionCode";

    private static int FIELD_TYPE_TOKEN = 0;
    private static int FIELD_TYPE_EDITION = 1;

    private SetImageGetter setImageGetter;

    public static SearchViewCursorAdapter empty(Context context) {
        return new SearchViewCursorAdapter(context, convert(new ArrayList<Option>()), 0);
    }

    public static SearchViewCursorAdapter from(Context context, List<Option> data) {
        return new SearchViewCursorAdapter(context, convert(data), 0);
    }

    private SearchViewCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        setImageGetter = new SetImageGetter(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder;
        View v;
        LayoutInflater inflater = LayoutInflater.from(context);

        String editionCode = getEditionCode(cursor);
        if (editionCode != null) {
            v = inflater.inflate(R.layout.searchview_adapter_edition, parent, false);
            holder = new EditionViewHolder(
                    (TextView) v.findViewById(R.id.searchview_edition_text),
                    (ImageView) v.findViewById(R.id.searchview_edition_image)
            );
        } else {
            v = inflater.inflate(R.layout.searchview_adapter_token, parent, false);
            holder = new TokenViewHolder((TextView) v);
        }

        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        String text = cursor.getString(cursor.getColumnIndex(FIELD_TEXT));
        if (holder instanceof TokenViewHolder) {
            TokenViewHolder tokenViewHolder = (TokenViewHolder) holder;
            tokenViewHolder.textView.setText(text);
        } else if (holder instanceof EditionViewHolder) {
            EditionViewHolder editionViewHolder = (EditionViewHolder) holder;
            editionViewHolder.textView.setText(text);
            Drawable drawable = setImageGetter.getDrawable(getEditionCode(cursor));
            editionViewHolder.imageView.setImageDrawable(drawable);
        }
    }

    interface ViewHolder {
    }

    private static class TokenViewHolder implements ViewHolder {
        TextView textView;

        TokenViewHolder(TextView textView) {
            this.textView = textView;
        }
    }

    private static class EditionViewHolder implements ViewHolder {
        TextView textView;
        ImageView imageView;

        EditionViewHolder(TextView textView, ImageView imageView) {
            this.textView = textView;
            this.imageView = imageView;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getType(cursor);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void changeCursor(List<Option> data) {
        super.changeCursor(convert(data));
    }

    private static Cursor convert(List<Option> data) {
        MatrixCursor cursor = new MatrixCursor(new String[]{"_id", FIELD_TYPE, FIELD_TEXT, FIELD_EDITION_CODE});
        for (int i = 0; i < data.size(); i++) {
            Option option = data.get(i);
            int type = FIELD_TYPE_TOKEN;
            String editionCode = null;
            if (option.getPayload() != null) {
                type = FIELD_TYPE_EDITION;
                editionCode = option.getPayload().getStdEditionCode();
            }
            cursor.addRow(new Object[]{i, type, option.getText(), editionCode});
        }
        return cursor;
    }

    private String getEditionCode(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(FIELD_EDITION_CODE));
    }

    private int getType(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(FIELD_TYPE));
    }
}
