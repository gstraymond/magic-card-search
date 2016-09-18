package fr.gstraymond.tools;

import android.content.Context;

import fr.gstraymond.R;
import fr.gstraymond.search.model.response.Card;

public class TypeFormatter {

    private static final String QUADRAT = "—";
    private static final String SEP = "--";

    private Context context;

    public TypeFormatter(Context context) {
        this.context = context;
    }

    public String format(Card card) {
        return card.getType().replaceAll(SEP, QUADRAT);
    }

    public String formatFirst(Card card) {
        return card.getType().split(SEP)[0];
    }
}
