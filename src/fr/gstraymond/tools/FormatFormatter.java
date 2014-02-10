package fr.gstraymond.tools;

import android.content.Context;
import android.text.TextUtils;
import fr.gstraymond.R;
import fr.gstraymond.magicsearch.model.response.MagicCard;

public class FormatFormatter {
	
	private Context context;

	public FormatFormatter(Context context) {
		this.context = context;
	}

	public String format(MagicCard card) {
		String formats = TextUtils.join(", ", card.getFormats());
		if (TextUtils.isEmpty(formats)) {
			formats = "Banned";
		}
		return context.getString(R.string.card_formats) + " " + formats;
	}
}
