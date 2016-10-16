package fr.gstraymond.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.gstraymond.R;
import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.biz.CastingCostImageGetter;
import fr.gstraymond.biz.Facets;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SetImageGetter;
import fr.gstraymond.constants.FacetConst;
import fr.gstraymond.glide.CardLoader;
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
    private Callbacks callbacks;

    private DateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());

    public interface Callbacks {
        void onImageClick(int position);
    }

    public SetArrayAdapter(Context context,
                           int resource,
                           int textViewResourceId,
                           List<Object> objects,
                           Callbacks callbacks) {
        super(context, resource, textViewResourceId, objects);
        this.castingCostFormatter = new CastingCostFormatter();
        this.descFormatter = new DescriptionFormatter();
        this.formatFormatter = new FormatFormatter(context);
        this.ptFormatter = new PowerToughnessFormatter();
        this.typeFormatter = new TypeFormatter(context);
        this.setImageGetter = new SetImageGetter(context);
        this.castingCostImageGetter = new CastingCostImageGetter(getAssetLoader());
        this.callbacks = callbacks;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Object object = getItem(position);

        // FIXME use getItemViewType / getItemViewTypeCount
        if (object instanceof Card) {
            View detail = getLayoutInflater().inflate(R.layout.card_detail, null);
            final Card card = (Card) object;
            TextView ccptView = (TextView) detail.findViewById(R.id.card_textview_ccpt);
            TextView typeView = (TextView) detail.findViewById(R.id.card_textview_type);
            ImageView pictureView = (ImageView) detail.findViewById(R.id.card_picture);
            TextView descView = (TextView) detail.findViewById(R.id.card_textview_description);
            TextView formatsView = (TextView) detail.findViewById(R.id.card_textview_formats);
            Button altView = (Button) detail.findViewById(R.id.card_alt);

            Spanned ccpt = formatCCPT(card);
            if (ccpt.toString().isEmpty()) ccptView.setVisibility(View.GONE);
            else ccptView.setText(ccpt);

            String type = typeFormatter.format(card);
            if (type.isEmpty()) typeView.setVisibility(View.GONE);
            else typeView.setText(type);

            String url = null;
            int urlPosition = 0;
            for (int i = 0; i < card.getPublications().size(); i++) {
                Publication publication = card.getPublications().get(i);
                if (publication.getImage() != null) {
                    url = publication.getImage();
                    urlPosition = i;
                    break;
                }
            }

            if (url != null) {
                new CardLoader(url, card, pictureView).load(getContext());
            } else {
                pictureView.setVisibility(View.GONE);
            }

            final int finalPosition = urlPosition;
            pictureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callbacks.onImageClick(finalPosition);
                }
            });

            formatsView.setText(formatFormatter.format(card));

            String desc = descFormatter.format(card, true);
            if (desc.isEmpty()) descView.setVisibility(View.GONE);
            else descView.setText(Html.fromHtml(desc, castingCostImageGetter, null));

            if (card.getAltTitles().isEmpty()) {
                altView.setVisibility(View.GONE);
            } else {
                altView.setText(TextUtils.join("\n", card.getAltTitles()));
                altView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CardListActivity.class);
                        Facets facets = new Facets();
                        ArrayList<String> layouts = new ArrayList<>();
                        layouts.add(card.getLayout());
                        facets.put(FacetConst.LAYOUT,  layouts);
                        SearchOptions options = new SearchOptions().setQuery(card.getTitle()).setFacets(facets);
                        intent.putExtra(CardListActivity.SEARCH_QUERY, options);
                        getContext().startActivity(intent);
                    }
                });
            }

            return detail;
        } else {
            View set = getLayoutInflater().inflate(R.layout.card_set, null);
            Publication publication = (Publication) object;
            ImageView publicationImage = (ImageView) set.findViewById(R.id.card_textview_set_image);
            TextView publicationImageAlt = (TextView) set.findViewById(R.id.card_textview_set_image_alt);
            TextView publicationText = (TextView) set.findViewById(R.id.card_textview_set_text);
            TextView publicationYear = (TextView) set.findViewById(R.id.card_textview_set_year);
            TextView publicationPrice = (TextView) set.findViewById(R.id.card_textview_set_price);
            Drawable setDrawable = setImageGetter.getDrawable(publication);

            if (setDrawable == null) {
                publicationImage.setVisibility(View.GONE);
                publicationImageAlt.setVisibility(View.VISIBLE);
                publicationImageAlt.setText("?");
            } else {
                publicationImageAlt.setVisibility(View.GONE);
                publicationImage.setVisibility(View.VISIBLE);
                publicationImage.setImageDrawable(setDrawable);
            }
            if (publication.getEditionReleaseDate() != null) {
                publicationYear.setText(dateFormat.format(publication.getEditionReleaseDate()));
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
        if (pt.isEmpty() && card.getLoyalty() != null) pt = card.getLoyalty();
        return Html.fromHtml(formatCC_PT(cc, pt), castingCostImageGetter, null);
    }

    private String formatCC_PT(String cc, String pt) {
        if (cc.isEmpty()) {
            return pt;
        }
        if (pt.isEmpty()) {
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
