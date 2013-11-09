package fr.gstraymond.magicsearch.model.request.facet;

public class Facet {

	private Term terms;

	public Facet(String facet) {
		this.terms = new Term(facet);
	}

	public Term getTerms() {
		return terms;
	}

	public void setTerms(Term terms) {
		this.terms = terms;
	}

}
