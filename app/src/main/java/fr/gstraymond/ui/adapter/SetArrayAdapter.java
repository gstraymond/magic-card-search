package fr.gstraymond.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.gstraymond.R;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.biz.CastingCostImageGetter;
import fr.gstraymond.biz.SetImageGetter;
import fr.gstraymond.search.model.response.Card;
import fr.gstraymond.search.model.response.Publication;
import fr.gstraymond.tools.CastingCostFormatter;
import fr.gstraymond.tools.DescriptionFormatter;
import fr.gstraymond.tools.FormatFormatter;
import fr.gstraymond.tools.PowerToughnessFormatter;
import fr.gstraymond.tools.TypeFormatter;
import fr.gstraymond.ui.CastingCostAssetLoader;

public class SetArrayAdapter extends ArrayAdapter<Object> {

    private CastingCostFormatter castingCostFormatter;
    private DescriptionFormatter descFormatter;
    private FormatFormatter formatFormatter;
    private PowerToughnessFormatter ptFormatter;
    private TypeFormatter typeFormatter;

    private SetImageGetter setImageGetter;
    private Html.ImageGetter castingCostImageGetter;

    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

    public SetArrayAdapter(Context context, int resource,
                           int textViewResourceId, List<Object> objects) {
        super(context, resource, textViewResourceId, objects);
        this.castingCostFormatter = new CastingCostFormatter();
        this.descFormatter = new DescriptionFormatter();
        this.formatFormatter = new FormatFormatter(context);
        this.ptFormatter = new PowerToughnessFormatter();
        this.typeFormatter = new TypeFormatter(context);
        this.setImageGetter = new SetImageGetter(context);
        this.castingCostImageGetter = new CastingCostImageGetter(getAssetLoader());
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Object object = getItem(position);

        if (object instanceof Card) {
            View detail = getLayoutInflater().inflate(R.layout.card_detail, null);
            Card card = (Card) object;
            TextView ccptView = (TextView) detail.findViewById(R.id.card_textview_ccpt);
            TextView typeView = (TextView) detail.findViewById(R.id.card_textview_type);
            TextView descView = (TextView) detail.findViewById(R.id.card_textview_description);
            TextView formatsView = (TextView) detail.findViewById(R.id.card_textview_formats);

            Spanned ccpt = formatCCPT(card);
            if (ccpt.toString().isEmpty()) ccptView.setVisibility(View.GONE);
            else ccptView.setText(ccpt);

            String type = typeFormatter.format(card);
            if (type.isEmpty()) typeView.setVisibility(View.GONE);
            else typeView.setText(type);

            formatsView.setText(formatFormatter.format(card));

            String desc = descFormatter.format(card, true);
            if (desc.isEmpty()) descView.setVisibility(View.GONE);
            else descView.setText(Html.fromHtml(desc, castingCostImageGetter, null));

            return detail;
        } else {
            View set = getLayoutInflater().inflate(R.layout.card_set, null);
            Publication publication = (Publication) object;
            ImageView publicationImage = (ImageView) set.findViewById(R.id.card_textview_set_image);
            TextView publicationText = (TextView) set.findViewById(R.id.card_textview_set_text);
            TextView publicationYear = (TextView) set.findViewById(R.id.card_textview_set_year);
            TextView publicationPrice = (TextView) set.findViewById(R.id.card_textview_set_price);
            Drawable setDrawable = setImageGetter.getDrawable(publication);

            if (setDrawable == null) {
                publicationImage.setVisibility(View.GONE);
            } else {
                publicationImage.setVisibility(View.VISIBLE);
                publicationImage.setImageDrawable(setDrawable);
            }
            if (publication.getEditionReleaseDate() != null) {
                publicationYear.setText(yearFormat.format(publication.getEditionReleaseDate()));
            } else {
                publicationYear.setText("");
            }
            publicationText.setText(publication.getEdition());
            String price = formatPrice(publication);
            if (price.equals("")) {
                publicationPrice.setVisibility(View.GONE);
            } else {
                publicationPrice.setVisibility(View.VISIBLE);
                publicationPrice.setText(price);
            }

            return set;
        }
    }

    private Spanned formatCCPT(Card card) {
        String cc = formatCC(card);
        String pt = ptFormatter.format(card);
        return Html.fromHtml(formatCC_PT(cc, pt), castingCostImageGetter, null);
    }

    private String formatCC_PT(String cc, String pt) {
        if (cc.length() == 0) {
            return pt;
        }
        if (pt.length() == 0) {
            return cc;
        }
        return cc + " — " + pt;
    }

    private CastingCostAssetLoader getAssetLoader() {
        CustomApplication application = (CustomApplication) getContext().getApplicationContext();
        return application.getCastingCostAssetLoader();
    }

    private String formatCC(Card card) {
        if (card.getCastingCost() == null) {
            return "";
        }
        return castingCostFormatter.format(card.getCastingCost());
    }

    private String formatPrice(Publication publication) {
        double price = publication.getPrice();
        if (price == 0d) return "";

        BigDecimal bd = new BigDecimal(price).round(new MathContext(2, RoundingMode.HALF_EVEN));
        return "$" + bd.toPlainString();
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }
}
