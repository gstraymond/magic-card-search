package fr.gstraymond.search.model.request;

public class QueryMatchAll {
	
	private Match_all match_all;
	
	public QueryMatchAll() {
		match_all = new Match_all();
	}

	public Match_all getMatch_all() {
		return match_all;
	}

	public void setMatch_all(Match_all match_all) {
		this.match_all = match_all;
	}
}