package fr.gstraymond.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magic.card.search.commons.log.Log;

import java.util.ArrayList;
import java.util.List;

import fr.gstraymond.R;
import fr.gstraymond.autocomplete.response.Option;
import fr.gstraymond.autocomplete.response.Payload;
import fr.gstraymond.biz.SetImageGetter;
import fr.gstraymond.tools.CardColorUtil;


public class SearchViewCursorAdapter extends CursorAdapter {

    private static String FIELD_TYPE = "type";
    private static String FIELD_TEXT = "text";
    private static String FIELD_EDITION_CODE = "editionCode";
    private static String FIELD_CARD_COLOR_ID = "cardColorId";
    private static String FIELD_CARD_TYPE = "cardType";

    private static int FIELD_TYPE_TOKEN = 0;
    private static int FIELD_TYPE_EDITION = 1;
    private static int FIELD_TYPE_CARD = 2;

    private SetImageGetter setImageGetter;
    private Log log = new Log(this);

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

        int type = getType(cursor);
        if (type == FIELD_TYPE_EDITION) {
            v = inflater.inflate(R.layout.searchview_adapter_edition, parent, false);
            holder = new EditionViewHolder(
                    (TextView) v.findViewById(R.id.searchview_edition_text),
                    (ImageView) v.findViewById(R.id.searchview_edition_image)
            );
        } else if (type == FIELD_TYPE_CARD) {
            v = inflater.inflate(R.layout.searchview_adapter_card, parent, false);
            holder = new CardViewHolder(
                    (TextView) v.findViewById(R.id.searchview_card_title),
                    (TextView) v.findViewById(R.id.searchview_card_type)
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

        if (holder instanceof EditionViewHolder) {
            EditionViewHolder editionViewHolder = (EditionViewHolder) holder;
            editionViewHolder.textView.setText(text);
            Drawable drawable = setImageGetter.getDrawable(getEditionCode(cursor));
            editionViewHolder.imageView.setImageDrawable(drawable);
        }

        if (holder instanceof CardViewHolder) {
            CardViewHolder cardViewHolder = (CardViewHolder) holder;
            int colorId = ResourcesCompat.getColor(context.getResources(), getCardColorId(cursor), null);
            cardViewHolder.textViewTitle.setTextColor(colorId);
            cardViewHolder.textViewTitle.setText(text);
            cardViewHolder.textViewType.setText(getCardType(cursor));
        }

        if (holder instanceof TokenViewHolder) {
            TokenViewHolder tokenViewHolder = (TokenViewHolder) holder;
            tokenViewHolder.textView.setText(text);
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

    private static class CardViewHolder implements ViewHolder {
        TextView textViewTitle;
        TextView textViewType;

        CardViewHolder(TextView textViewTitle, TextView textViewType) {
            this.textViewTitle = textViewTitle;
            this.textViewType = textViewType;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getType(cursor);
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    public void changeCursor(List<Option> data) {
        super.changeCursor(convert(data));
    }

    private static Cursor convert(List<Option> data) {
        MatrixCursor cursor = new MatrixCursor(
                new String[]{
                        "_id",
                        FIELD_TYPE,
                        FIELD_TEXT,
                        FIELD_EDITION_CODE,
                        FIELD_CARD_COLOR_ID,
                        FIELD_CARD_TYPE
                }
        );

        for (int i = 0; i < data.size(); i++) {
            Option option = data.get(i);
            int type = FIELD_TYPE_TOKEN;
            String editionCode = null;
            Integer cardColorId = null;
            String cardType = null;
            Payload payload = option.getPayload();
            if (payload != null) {
                if (payload.getStdEditionCode() != null) {
                    type = FIELD_TYPE_EDITION;
                    editionCode = payload.getStdEditionCode();
                } else {
                    type = FIELD_TYPE_CARD;
                    cardColorId =  CardColorUtil.getColorId(payload.getColors(), payload.getType());
                    cardType = payload.getType();
                }
            }
            cursor.addRow(new Object[]{
                    i,
                    type,
                    option.getText(),
                    editionCode,
                    cardColorId,
                    cardType
            });
        }
        return cursor;
    }

    private String getEditionCode(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(FIELD_EDITION_CODE));
    }

    private int getType(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(FIELD_TYPE));
    }

    private int getCardColorId(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(FIELD_CARD_COLOR_ID));
    }

    private String getCardType(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(FIELD_CARD_TYPE));
    }
}
