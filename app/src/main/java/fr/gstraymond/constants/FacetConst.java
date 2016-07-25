package fr.gstraymond.constants;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.gstraymond.R;
import fr.gstraymond.search.model.request.facet.Facet;

public class FacetConst {

    private static final String ABILITY = "abilities.exact";
    private static final String ARTIST = "artists.exact";
    private static final String CMC = "convertedManaCost";
    private static final String COLOR = "colors.exact";
    private static final String DEVOTION = "devotions";
    private static final String SET = "editions.exact";
    private static final String FORMAT = "formats.exact";
    private static final String POWER = "power";
    private static final String RARITY = "rarities";
    private static final String TOUGHNESS = "toughness";
    private static final String TYPE = "type";
    private static final String PRICE = "priceRanges.exact";
    private static final String BLOCK = "blocks.exact";
    private static final String LAYOUT = "layout.exact";

    private static Map<String, Integer> facetNames;
    private static List<String> facetOrder;

    static {
        facetNames = new HashMap<>();
        facetNames.put(ABILITY, R.string.facet_ability);
        facetNames.put(ARTIST, R.string.facet_artist);
        facetNames.put(COLOR, R.string.facet_color);
        facetNames.put(CMC, R.string.facet_cmc);
        facetNames.put(DEVOTION, R.string.facet_devotion);
        facetNames.put(FORMAT, R.string.facet_format);
        facetNames.put(POWER, R.string.facet_power);
        facetNames.put(RARITY, R.string.facet_rarity);
        facetNames.put(SET, R.string.facet_set);
        facetNames.put(BLOCK, R.string.block_set);
        facetNames.put(TOUGHNESS, R.string.facet_toughness);
        facetNames.put(TYPE, R.string.facet_type);
        facetNames.put(LAYOUT, R.string.layout_type);
        facetNames.put(PRICE, R.string.facet_price);

        facetOrder = new ArrayList<>();
        facetOrder.add(COLOR);
        facetOrder.add(TYPE);
        facetOrder.add(LAYOUT);
        facetOrder.add(FORMAT);
        facetOrder.add(CMC);
        facetOrder.add(RARITY);
        facetOrder.add(SET);
        facetOrder.add(BLOCK);
        facetOrder.add(ABILITY);
        facetOrder.add(DEVOTION);
        facetOrder.add(POWER);
        facetOrder.add(TOUGHNESS);
        facetOrder.add(PRICE);
        facetOrder.add(ARTIST);

    }

    private static void putInFacets(Map<String, Facet> facets, String facet) {
        facets.put(facet, new Facet(facet));
    }

    public static Map<String, Facet> getFacets() {
        Map<String, Facet> facets = new HashMap<>();
        putInFacets(facets, ABILITY);
        putInFacets(facets, ARTIST);
        putInFacets(facets, BLOCK);
        putInFacets(facets, COLOR);
        putInFacets(facets, CMC);
        putInFacets(facets, DEVOTION);
        putInFacets(facets, FORMAT);
        putInFacets(facets, LAYOUT);
        putInFacets(facets, POWER);
        putInFacets(facets, PRICE);
        putInFacets(facets, RARITY);
        putInFacets(facets, TOUGHNESS);
        putInFacets(facets, TYPE);
        putInFacets(facets, SET);
        return facets;
    }

    public static String getFacetName(String facet, Context context) {
        Integer resId = facetNames.get(facet);
        if (resId == null) return null;

        return context.getString(resId);
    }

    public static List<String> getFacetOrder() {
        return facetOrder;
    }
}
