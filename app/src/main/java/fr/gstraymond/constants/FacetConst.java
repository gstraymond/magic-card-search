package fr.gstraymond.constants;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.gstraymond.R;
import fr.gstraymond.models.search.request.facet.Facet;

public class FacetConst {

    public static final String ABILITY = "abilities.exact";
    public static final String ARTIST = "artists.exact";
    public static final String CMC = "convertedManaCost";
    public static final String COLOR = "colors.exact";
    public static final String DUAL_COLOR = "dualColors.exact";
    public static final String TRIPLE_COLOR = "tripleColors.exact";
    public static final String DEVOTION = "devotions";
    public static final String SET = "editions.exact";
    public static final String FORMAT = "formats.exact";
    public static final String POWER = "power";
    public static final String RARITY = "rarities";
    public static final String TOUGHNESS = "toughness";
    public static final String TYPE = "type";
    public static final String PRICE = "priceRanges.exact";
    public static final String BLOCK = "blocks.exact";
    public static final String LAYOUT = "layout.exact";
    public static final String LAND = "land.exact";
    public static final String SPECIAL = "special.exact";

    private static Map<String, Integer> facetNames;
    private static List<String> facetOrder;

    static {
        facetNames = new HashMap<>();
        facetNames.put(ABILITY, R.string.facet_ability);
        facetNames.put(ARTIST, R.string.facet_artist);
        facetNames.put(COLOR, R.string.facet_color);
        facetNames.put(DUAL_COLOR, R.string.facet_dual_color);
        facetNames.put(TRIPLE_COLOR, R.string.facet_triple_color);
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
        facetNames.put(LAND, R.string.facet_land);
        facetNames.put(SPECIAL, R.string.facet_special);

        facetOrder = new ArrayList<>();
        facetOrder.add(COLOR);
        facetOrder.add(DUAL_COLOR);
        facetOrder.add(TRIPLE_COLOR);
        facetOrder.add(TYPE);
        facetOrder.add(LAND);
        facetOrder.add(LAYOUT);
        facetOrder.add(FORMAT);
        facetOrder.add(CMC);
        facetOrder.add(RARITY);
        facetOrder.add(SET);
        facetOrder.add(BLOCK);
        facetOrder.add(SPECIAL);
        facetOrder.add(ABILITY);
        facetOrder.add(DEVOTION);
        facetOrder.add(POWER);
        facetOrder.add(TOUGHNESS);
        facetOrder.add(PRICE);
        facetOrder.add(ARTIST);

    }

    private static void putInFacets(Map<String, Facet> facets, String facet) {
        facets.put(facet, Facet.Companion.fromField(facet));
    }

    public static Map<String, Facet> getFacets() {
        Map<String, Facet> facets = new HashMap<>();
        putInFacets(facets, ABILITY);
        putInFacets(facets, ARTIST);
        putInFacets(facets, BLOCK);
        putInFacets(facets, COLOR);
        putInFacets(facets, DUAL_COLOR);
        putInFacets(facets, TRIPLE_COLOR);
        putInFacets(facets, CMC);
        putInFacets(facets, DEVOTION);
        putInFacets(facets, FORMAT);
        putInFacets(facets, LAND);
        putInFacets(facets, LAYOUT);
        putInFacets(facets, POWER);
        putInFacets(facets, PRICE);
        putInFacets(facets, RARITY);
        putInFacets(facets, SPECIAL);
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
