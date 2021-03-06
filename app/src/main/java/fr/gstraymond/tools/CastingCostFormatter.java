package fr.gstraymond.tools;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class CastingCostFormatter {

    public String format(String castingCost) {
        if (castingCost.isEmpty()) return "";

        List<String> costs = new ArrayList<>();
        if (castingCost.contains(" ")) {
            for (String cost : castingCost.split(" ")) {
                costs.add(replace(cost));
            }
        } else {
            costs.add(replace(castingCost));
        }

        return TextUtils.join("", costs);
    }

    private String replace(String cost) {
        String tempCost = cost;
        if (cost.contains("/")) {
            tempCost = cost.replace("/", "");
        }

        return "<img src='" + tempCost + ".png' />";
    }

}
