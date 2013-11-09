package fr.gstraymond.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.gstraymond.magicsearch.model.request.facet.Facet;

public class FacetConst {

	private static final String ABILITY = "abilities.exact";
	private static final String ARTIST = "artists.exact";
	private static final String CMC = "convertedManaCost";
	private static final String COLOR = "colors.exact";
	private static final String DEVOTION = "devotions";
	private static final String EDITION = "editions.exact";
	private static final String FORMAT = "formats";
	private static final String POWER = "power";
	private static final String RARITY = "rarities";
	private static final String TOUGHNESS = "toughness";
	private static final String TYPE = "type";

	private static Map<String, Facet> facets;
	private static Map<String, String> facetNames;
	private static List<String> facetOrder;

	static {
		facets = new HashMap<String, Facet>();
		putInFacets(ABILITY);
		putInFacets(ARTIST);
		putInFacets(COLOR);
		putInFacets(CMC);
		putInFacets(DEVOTION);
		putInFacets(EDITION);
		putInFacets(FORMAT);
		putInFacets(POWER);
		putInFacets(RARITY);
		putInFacets(TOUGHNESS);
		putInFacets(TYPE);

		facetNames = new HashMap<String, String>();
		facetNames.put(ABILITY, "Ability");
		facetNames.put(ARTIST, "Artist");
		facetNames.put(COLOR, "Color");
		facetNames.put(CMC, "Converted Mana Cost");
		facetNames.put(DEVOTION, "Devotion");
		facetNames.put(EDITION, "Edition");
		facetNames.put(FORMAT, "Format");
		facetNames.put(POWER, "Power");
		facetNames.put(RARITY, "Rarity");
		facetNames.put(TOUGHNESS, "Toughness");
		facetNames.put(TYPE, "Type");
		
		facetOrder = new ArrayList<String>();
		facetOrder.add(COLOR);
		facetOrder.add(DEVOTION);
		facetOrder.add(CMC);
		facetOrder.add(TYPE);
		facetOrder.add(ABILITY);
		facetOrder.add(POWER);
		facetOrder.add(TOUGHNESS);
		facetOrder.add(RARITY);
		facetOrder.add(EDITION);
		facetOrder.add(FORMAT);
		facetOrder.add(ARTIST);
	}

	private static void putInFacets(String facet) {
		facets.put(facet, new Facet(facet));
	}

	public static Map<String, Facet> getFacets() {
		return facets;
	}

	public static String getFacetName(String facet) {
		return facetNames.get(facet);
	}
	
	public static List<String> getFacetOrder() {
		return facetOrder;
	}
}
