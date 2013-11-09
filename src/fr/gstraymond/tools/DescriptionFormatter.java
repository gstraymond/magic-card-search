package fr.gstraymond.tools;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DescriptionFormatter {
	
	private Pattern pattern;
	
	public DescriptionFormatter() {
		this.pattern = Pattern.compile("\\{(.*?)\\}");
	}
	
	public String format(String description) {
		String tmpDescription = description;
		if (description.contains("{")) {

			Matcher matcher = pattern.matcher(description);
			while (matcher.find()) {
				String match = matcher.group();
				String espacedMatch = match
						.replace("{", "\\{").replace("}", "\\}")
						.replace("(", "\\(").replace(")", "\\)");

				tmpDescription = tmpDescription.replaceFirst(espacedMatch, replace(match));
			}
		}
		
		if (! tmpDescription.contains("\n")) {
			return "<p>" + tmpDescription + "</p>";
		}
		
		StringBuffer lines = new StringBuffer();
		for (String line : tmpDescription.split("\n")) {
			lines.append("<p>").append(line).append("</p>");
		}
		return lines.toString();
	}

	private String replace(String cost) {		
		// cas pour les LEVEL X-Y
		if (cost.contains("LEVEL")) {
			return cost;
		}
		
		String tempCost = cost.toUpperCase(Locale.ENGLISH);

		tempCost = tempCost.replace("{", "");
		tempCost = tempCost.replace("}", "");

		tempCost = tempCost.replace("(", "");
		tempCost = tempCost.replace(")", "");
		
		tempCost = tempCost.replace("/", "");

		return formatManaSymbol(tempCost);
	}

	private String formatManaSymbol(String tempCost) {
		return "<img src='" + tempCost + ".jpeg' />";
	}

}
