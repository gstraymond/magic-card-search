package fr.gstraymond.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    private Html.ImageGetter setImageGetter;
    private Html.ImageGetter castingCostImageGetter;

    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy — ", Locale.getDefault());

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
            TextView text = (TextView) detail.findViewById(R.id.card_textview_detail);
            text.setText(formatCard(card));
            return detail;
        } else {
            View set = getLayoutInflater().inflate(R.layout.card_set, null);
            Publication publication = (Publication) object;
            TextView publicationImage = (TextView) set.findViewById(R.id.card_textview_set_image);
            TextView publicationText = (TextView) set.findViewById(R.id.card_textview_set_text);
            Spanned imageText = formatPublicationImage(publication);
            if (TextUtils.isEmpty(imageText)) {
                publicationImage.setVisibility(View.GONE);
            } else {
                publicationImage.setVisibility(View.VISIBLE);
                publicationImage.setText(imageText);
            }
            String year = "";
            if (publication.getEditionReleaseDate() != null) {
                year = yearFormat.format(publication.getEditionReleaseDate());
            }
            publicationText.setText(year + publication.getEdition() + formatPrice(publication));
            return set;
        }
    }

    private Spanned formatCard(Card card) {
        String cc = formatCC(card);
        String pt = ptFormatter.format(card);
        String cc_pt = formatCC_PT(cc, pt);
        String type = typeFormatter.format(card);
        String description = descFormatter.format(card);
        String formats = formatFormatter.format(card);
        String html = getHtml(cc_pt, type, description, formats);

        return Html.fromHtml(html, castingCostImageGetter, null);
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

    private String getHtml(String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            if (!string.isEmpty() && !builder.toString().isEmpty()) {
                builder.append("<br /><br />");
            }
            builder.append(string);
        }
        return builder.toString();
    }

    private String formatCC(Card card) {
        if (card.getCastingCost() == null) {
            return "";
        }
        return castingCostFormatter.format(card.getCastingCost());
    }

    private Spanned formatPublicationImage(Publication publication) {
        String editionImage = getEditionImage(publication);
        return Html.fromHtml(editionImage, setImageGetter, null);
    }

    private String getEditionImage(Publication pub) {
        if (pub.getStdEditionCode() == null) {
            return "";
        }

        return "<img src='" + pub.getStdEditionCode() + "/" + pub.getRarityCode() + ".png' />";
    }

    private String formatPrice(Publication publication) {
        double price = publication.getPrice();
        if (price == 0d) return "";

        BigDecimal bd = new BigDecimal(price).round(new MathContext(2, RoundingMode.HALF_EVEN));
        return " — $" + bd.toPlainString();
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }
}
