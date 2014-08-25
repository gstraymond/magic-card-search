package fr.gstraymond.db;

import java.text.ParseException;
import java.util.Date;

import android.text.TextUtils;

public class CardHistory {

	private static final String SEP = ";";
	private int id;
	private String query;
	private Date date;
	private boolean favorite;
	
	public CardHistory(int id, String query, boolean favorite) {
		this.id = id;
		this.query = query;
		this.date = new Date();
		this.favorite = favorite;
	}
		
	public CardHistory(String line) throws ParseException {
		String[] split = line.split(SEP);
		this.id = Integer.parseInt(split[0]);
		this.query = split[1];
		this.date = new Date(Long.parseLong(split[2]));
		this.favorite = split[3].equals(1);
	}

	@Override
	public String toString() {
		String[] strings = { 
			id + "", 
			query,
			date.getTime() + "",
			favorite ? "1" : "0"
		};
		return TextUtils.join(SEP, strings);
	}

	protected int getId() {
		return id;
	}

	protected String getQuery() {
		return query;
	}

	protected Date getDate() {
		return date;
	}

	protected boolean isFavorite() {
		return favorite;
	}
}
