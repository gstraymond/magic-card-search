package fr.gstraymond.tools;

import fr.gstraymond.models.response.Card;

public class TypeFormatter {

    private static final String QUADRAT = "—";
    private static final String SEP = "--";

    public String format(Card card) {
        return card.getType().replaceAll(SEP, QUADRAT);
    }

    public String formatFirst(Card card) {
        return card.getType().split(SEP)[0];
    }
}
