package fr.gstraymond.tools;

import fr.gstraymond.magicsearch.model.response.MagicCard;

public class PowerToughnessFormatter {

	public String format(MagicCard card) {
		if (card.getPower() == null) {
			return "";
		}
		return card.getPower() + " / " + card.getToughness();
	}
}
