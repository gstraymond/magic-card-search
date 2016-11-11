package fr.gstraymond.db.json;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import fr.gstraymond.android.CustomParcelable;
import fr.gstraymond.biz.Facets;

public class JsonHistory extends CustomParcelable {

    private String query;
    private Date date;
    private boolean favorite;
    private Facets facets;

    JsonHistory() {
    }

    JsonHistory(String query, boolean favorite, Facets facets) {
        this.query = query;
        this.date = new Date();
        this.favorite = favorite;
        this.facets = facets;
    }

    public static final Parcelable.Creator<JsonHistory> CREATOR = new Parcelable.Creator<JsonHistory>() {
        @Override
        public JsonHistory createFromParcel(Parcel source) {
            return new JsonHistory(source);
        }

        @Override
        public JsonHistory[] newArray(int size) {
            return new JsonHistory[size];
        }
    };

    private JsonHistory(Parcel source) {
        query = source.readString();
        date = new Date(source.readLong());
        favorite = source.readString().equals("1");
        facets = readFacets(source);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(query);
        dest.writeLong(date.getTime());
        dest.writeString(favorite ? "1" : "0");
        writeFacets(dest, facets);
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Facets getFacets() {
        return facets;
    }

    public void setFacets(Facets facets) {
        this.facets = facets;
    }
}
