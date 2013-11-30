package fr.gstraymond.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class SearchOptions implements Parcelable {

	private String query;
	private boolean append = false;
	private boolean random = false;
	private int from = 0;
	private int size = 20;
	private Map<String, List<String>> facets = new HashMap<String, List<String>>();


	public static final Parcelable.Creator<SearchOptions> CREATOR = new Parcelable.Creator<SearchOptions>() {
		@Override
		public SearchOptions createFromParcel(Parcel source) {
			return new SearchOptions(source);
		}

		@Override
		public SearchOptions[] newArray(int size) {
			return new SearchOptions[size];
		}
	};

	public SearchOptions(Parcel source) {
		query = source.readString();
		append = source.readInt() == 0 ? true : false;
		random = source.readInt() == 0 ? true : false;
		from = source.readInt();
		size = source.readInt();
		// readMap
	}

	public SearchOptions() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(query);
		dest.writeInt(append ? 0 : 1);
		dest.writeInt(random ? 0 : 1);
		dest.writeInt(from);
		dest.writeInt(size);
		writeMap(dest);
	}

	private void writeMap(Parcel dest) {
		// TODO Auto-generated method stub
		
	}

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
