package fr.gstraymond.tools;

import android.content.Context;
import fr.gstraymond.R;
import fr.gstraymond.magicsearch.model.response.MagicCard;

public class TypeFormatter {

	private static final String QUADRAT = "—";
	private static final String SEP = "--";
	
	private Context context;

	public TypeFormatter(Context context) {
		this.context = context;
	}

	public String format(MagicCard card) {
		String type = card.getType().replaceAll(SEP, QUADRAT);
		return context.getString(R.string.card_type) + " " + type;
	}

	public String formatFirst(MagicCard card) {
		return card.getType().split(SEP)[0];
	}
}
