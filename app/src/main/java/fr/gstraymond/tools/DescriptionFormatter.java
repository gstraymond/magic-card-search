package fr.gstraymond.tools;

import android.text.TextUtils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.gstraymond.constants.Abilities;
import fr.gstraymond.constants.KeywordAbilities;
import fr.gstraymond.constants.Zones;
import fr.gstraymond.models.search.response.Card;

public class DescriptionFormatter {

    private final Pattern pattern;

    public DescriptionFormatter() {
        this.pattern = Pattern.compile("\\{(.*?)\\}");
    }

    public String format(Card card, Boolean highlight) {
        if ("".equals(card.getDescription())) {
            return "";
        }

        String tmp = card.getDescription().replaceAll("--", "—");
        if (tmp.contains("{")) {

            Matcher matcher = pattern.matcher(card.getDescription());
            while (matcher.find()) {
                String match = matcher.group();
                String espacedMatch = match
                        .replace("{", "\\{").replace("}", "\\}")
                        .replace("(", "\\(").replace(")", "\\)");

                tmp = tmp.replaceFirst(espacedMatch, replace(match));
            }
        }

        String join = TextUtils.join("<br /><br />", tmp.split("\n"));
        if (highlight) {
            /*for (String keywordAction: KeywordActions.ALL) {
                join = join.replaceAll(keywordAction + " ", "<b>" + keywordAction + "</b> ");
                join = join.replaceAll(keywordAction.lowercase() + " ", "<b>" + keywordAction.lowercase() + "</b> ");
            }*/
            for (String keywordAction : KeywordAbilities.ALL) {
                join = join.replaceAll(keywordAction, "<b>" + keywordAction + "</b>");
                join = join.replaceAll(", " + keywordAction.toLowerCase(), ", <b>" + keywordAction.toLowerCase() + "</b>");
                join = join.replaceAll(" " + keywordAction.toLowerCase(), " <b>" + keywordAction.toLowerCase() + "</b>");
            }

            for (String ability: Abilities.ALL) {
                join = join.replaceAll(ability, "<b>" + ability + "</b>");
            }

            for (String ability: Zones.ALL) {
                join = join.replaceAll(ability, "<b>" + ability + "</b>");
            }
        }
        return join;
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
        return "<img src='" + tempCost + ".png' />";
    }

}
