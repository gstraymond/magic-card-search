package fr.gstraymond.biz;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.gstraymond.android.CustomParcelable;

public class SearchOptions extends CustomParcelable {

    public static final String QUERY_ALL = "*";

    private String query = QUERY_ALL;
    private boolean append = false;
    private boolean random = false;
    private boolean addToHistory = true;
    private int from = 0;
    private int size = 30;
    private Facets facets = new Facets();
    private Map<String, Integer> facetSize = new HashMap<String, Integer>();


    public static final Parcelable.Creator<SearchOptions> CREATOR = new Parcelable.Creator<SearchOptions>() {
        @Override
        public SearchOptions createFromParcel(Parcel source) {
            return new SearchOptions(source);
        }

        @Override
        public SearchOptions[] newArray(int size) {
            return new SearchOptions[size];
        }
    };

    public SearchOptions(Parcel source) {
        query = source.readString();
        append = source.readInt() == 0;
        random = source.readInt() == 0;
        from = source.readInt();
        size = source.readInt();
        facets = readFacets(source);
        // facetSize : pas de persistence de la taille des facettes
        // addToHistory : pas de persistence de l'ajout à l'historique
    }

    public SearchOptions() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(query);
        dest.writeInt(append ? 0 : 1);
        dest.writeInt(random ? 0 : 1);
        dest.writeInt(from);
        dest.writeInt(size);
        writeFacets(dest, facets);
        // facetSize : pas de persistence de la taille des facettes
        // addToHistory : pas de persistence de l'ajout à l'historique
    }

    public String getQuery() {
        return query;
    }

    public SearchOptions setQuery(String query) {
        if (TextUtils.isEmpty(query)) {
            this.query = QUERY_ALL;
        } else {
            this.query = query;
        }
        return this;
    }

    public boolean isAppend() {
        return append;
    }

    public SearchOptions setAppend(boolean append) {
        this.append = append;
        return this;
    }

    public boolean isRandom() {
        return random;
    }

    public SearchOptions setRandom(boolean random) {
        this.random = random;
        return this;
    }

    public Integer getFrom() {
        return from;
    }

    public SearchOptions setFrom(Integer from) {
        this.from = from;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public SearchOptions setSize(Integer size) {
        this.size = size;
        return this;
    }

    public SearchOptions addFacet(String facet, String term) {
        if (facets.containsKey(facet)) {
            facets.get(facet).add(term);
        } else {
            List<String> terms = new ArrayList<String>();
            terms.add(term);
            facets.put(facet, terms);
        }
        return this;
    }

    public SearchOptions removeFacet(String facet, String term) {
        if (facets.containsKey(facet)) {
            facets.get(facet).remove(term);

            if (facets.get(facet).size() == 0) {
                facets.remove(facet);
            }
        }
        return this;
    }

    public SearchOptions addFacetSize(String facet) {
        if (facetSize.containsKey(facet)) {
            Integer size = facetSize.get(facet);
            facetSize.put(facet, size + 10);
        } else {
            facetSize.put(facet, 20);
        }
        return this;
    }

    public Facets getFacets() {
        return facets;
    }

    public SearchOptions setFacets(Facets facets) {
        this.facets = facets;
        return this;
    }

    public boolean isAddToHistory() {
        return addToHistory;
    }

    public SearchOptions setAddToHistory(boolean addToHistory) {
        this.addToHistory = addToHistory;
        return this;
    }

    public Map<String, Integer> getFacetSize() {
        return facetSize;
    }

    public void setFacetSize(Map<String, Integer> facetSize) {
        this.facetSize = facetSize;
    }

    @Override
    public String toString() {
        return String.format(
                "searchOptions:[query:%s, append:%s, random:%s, addToHistory:%s, from:%s, size:%s, facets:%s, facetSize:%S]",
                query,
                append,
                random,
                addToHistory,
                from,
                size,
                facets,
                facetSize);
    }
}
