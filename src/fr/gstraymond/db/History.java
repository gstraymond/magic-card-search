package fr.gstraymond.db;

import java.text.ParseException;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class History implements Parcelable {

	private static final String SEP = ";";
	private int id;
	private String query;
	private Date date;
	private boolean favorite;

	public static final Parcelable.Creator<History> CREATOR = new Parcelable.Creator<History>() {
		@Override
		public History createFromParcel(Parcel source) {
			return new History(source);
		}

		@Override
		public History[] newArray(int size) {
			return new History[size];
		}
	};

	public History(Parcel source) {
		id = source.readInt();
		query = source.readString();
		date = new Date(source.readLong());
		favorite = source.readString() == "1";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(query);
		dest.writeLong(date.getTime());
		dest.writeString(favorite ? "1" : "0");
	}
	
	public History(int id, String query, boolean favorite) {
		this.id = id;
		this.query = query;
		this.date = new Date();
		this.favorite = favorite;
	}
		
	public History(String line) throws ParseException {
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

	public int getId() {
		return id;
	}

	public String getQuery() {
		return query;
	}

	public Date getDate() {
		return date;
	}

	public boolean isFavorite() {
		return favorite;
	}
}
