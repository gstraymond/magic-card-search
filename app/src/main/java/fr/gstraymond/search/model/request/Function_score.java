package fr.gstraymond.search.model.request;

public class Function_score {

	private QueryMatchAll query;
	private Random_score random_score;

	public Function_score() {
		query = new QueryMatchAll();
		random_score = new Random_score();
	}

	public QueryMatchAll getQuery() {
		return query;
	}

	public void setQuery(QueryMatchAll query) {
		this.query = query;
	}

	public Object getRandom_score() {
		return random_score;
	}

	public void setRandom_score(Random_score random_score) {
		this.random_score = random_score;
	}
}
