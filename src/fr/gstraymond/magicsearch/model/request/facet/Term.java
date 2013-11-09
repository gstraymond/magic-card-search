package fr.gstraymond.magicsearch.model.request.facet;

public class Term {

	private String field;

	public Term(String field) {
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
}
