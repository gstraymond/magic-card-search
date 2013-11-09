package fr.gstraymond.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class SearchOptions {

	private String query;
	private boolean append = false;
	private boolean random = false;
	private Integer from = null;
	private Integer size = 20;
	private Map<String, List<String>> facets = new HashMap<String, List<String>>();

	public String getQuery() {
		return query;
	}

	public SearchOptions setQuery(String query) {
		this.query = query;
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
			Log.d(getClass().getName(), "removed term " + term);
			facets.get(facet).remove(term);
			
			if (facets.get(facet).size() == 0) {
				Log.d(getClass().getName(), "removed facet " + facet);
				facets.remove(facet);
			}
		}
		return this;
	}
	
	public Map<String, List<String>> getFacets() {
		return facets;
	}
	
	public SearchOptions setFacets(Map<String, List<String>> facets) {
		this.facets = facets;
		return this;
	}

	@Override
	public String toString() {
		return "search options:\n" +
				"query: " + query + "\n" +
				"append: " + append + "\n" +
				"random: " + random + "\n" +
				"from: " + from + "\n" +
				"size: " + size + "\n" +
				"facets:" + facets;
	}
}
