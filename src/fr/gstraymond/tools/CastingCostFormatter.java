package fr.gstraymond.tools;

import java.util.ArrayList;
import java.util.List;

public class CastingCostFormatter {

	public String format(String castingCost) {
		List<String> costs = new ArrayList<String>();
		if (castingCost.contains(" ")) {
			for (String cost : castingCost.split(" ")) {
				costs.add(replace(cost));
			}
		} else {
			costs.add(replace(castingCost));
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		for (String cost : costs) {
			stringBuilder.append(cost);
		}
		return stringBuilder.toString();
	}

	private String replace(String cost) {
		String tempCost = cost;
		if (cost.contains("/")) {
			tempCost = cost.replace("/", "");
		}

		return "<img src='" + tempCost + ".png' />";
	}

}
