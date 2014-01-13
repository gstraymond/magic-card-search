package fr.gstraymond.tools;

import fr.gstraymond.magicsearch.model.response.MagicCard;

public class TypeFormatter {

	private static final String QUADRAT = "—";
	private static final String SEP = "--";

	public String format(MagicCard card) {
		return card.getType().replaceAll(SEP, QUADRAT);
	}

	public String formatFirst(MagicCard card) {
		return card.getType().split(SEP)[0];
	}
}
