package fr.gstraymond.magicsearch.model.response;

import java.util.Map;

import fr.gstraymond.magicsearch.model.response.facet.Facet;

public class SearchResult {
	
	private int took;
	private Hits hits;
//	private Facets facets;
	private Map<String, Facet> facets;
	
	public int getTook() {
		return took;
	}
	
	public void setTook(int took) {
		this.took = took;
	}
	
	public Hits getHits() {
		return hits;
	}
	
	public void setHits(Hits hits) {
		this.hits = hits;
	}
//	
//	public Facets getFacets() {
//		return facets;
//	}
//	
//	public void setFacets(Facets facets) {
//		this.facets = facets;
//	}

	public Map<String, Facet> getFacets() {
		return facets;
	}

	public void setFacets(Map<String, Facet> facets) {
		this.facets = facets;
	}
}