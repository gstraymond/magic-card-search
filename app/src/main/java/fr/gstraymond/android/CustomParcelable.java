package fr.gstraymond.android;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CustomParcelable implements Parcelable {

    protected Map<String, List<String>> readFacets(Parcel source) {
        return readFacets(source.readString());
    }

    protected void writeFacets(Parcel dest, Map<String, List<String>> facets) {
        dest.writeString(writeFacets(facets));
    }

    protected Map<String, List<String>> readFacets(String facetsAsString) {
        Map<String, List<String>> facets = new HashMap<>();

        if (facetsAsString == null || facetsAsString.isEmpty()) {
            return facets;
        }

        String[] firstSplit = facetsAsString.split("\\|");
        for (String facet : firstSplit) {
            String[] keyValues = facet.split("=");
            String key = keyValues[0];

            String valuesAsString = keyValues[1];
            String[] secondSplit = valuesAsString.split(",");
            List<String> values = new ArrayList<>();
            Collections.addAll(values, secondSplit);

            facets.put(key, values);
        }
        return facets;
    }

    protected String writeFacets(Map<String, List<String>> facets) {
        StringBuilder facetsAsString = new StringBuilder();
        String firstSep = "";
        for (Map.Entry<String, List<String>> entry : facets.entrySet()) {
            facetsAsString.append(firstSep);
            facetsAsString.append(entry.getKey());
            facetsAsString.append("=");
            String secondSep = "";
            for (String value : entry.getValue()) {
                facetsAsString.append(secondSep);
                facetsAsString.append(value);
                secondSep = ",";
            }
            firstSep = "|";
        }
        return facetsAsString.toString();
    }
}
