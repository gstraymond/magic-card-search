package fr.gstraymond.android;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

import fr.gstraymond.utils.FacetParcelableUtils;

public abstract class CustomParcelable implements Parcelable {

    protected Map<String, List<String>> readFacets(Parcel source) {
        return readFacets(source.readString());
    }

    protected void writeFacets(Parcel dest, Map<String, List<String>> facets) {
        dest.writeString(writeFacets(facets));
    }

    protected Map<String, List<String>> readFacets(String facetsAsString) {
        return FacetParcelableUtils.INSTANCE.readFacets(facetsAsString);
    }

    protected String writeFacets(Map<String, List<String>> facets) {
        return FacetParcelableUtils.INSTANCE.writeFacets(facets);
    }
}
