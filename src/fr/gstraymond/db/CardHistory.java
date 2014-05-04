package fr.gstraymond.db;

import java.util.Date;

public class CardHistory {

	private int id;
	private String query;
	private Date date;
	private boolean favorite;

	protected int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected String getQuery() {
		return query;
	}

	protected void setQuery(String query) {
		this.query = query;
	}

	protected Date getDate() {
		return date;
	}

	protected void setDate(Date date) {
		this.date = date;
	}

	protected boolean isFavorite() {
		return favorite;
	}

	protected void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

}
