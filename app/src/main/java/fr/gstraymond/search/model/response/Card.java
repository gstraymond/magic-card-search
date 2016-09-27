package fr.gstraymond.search.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Card implements Parcelable {
    private String title;
    private String frenchTitle;
    private String type;
    private String castingCost;
    private String power;
    private String toughness;
    private String description;
    private List<Publication> publications;
    private List<String> formats;
    private List<String> colors;
    private String layout;
    private String loyalty;

    private List<String> altTitles;

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public Card() {
    }

    public Card(Parcel source) {
        title = source.readString();
        frenchTitle = source.readString();
        type = source.readString();
        castingCost = source.readString();
        power = source.readString();
        toughness = source.readString();
        description = source.readString();
        publications = new ArrayList<>();
        source.readList(publications, Publication.class.getClassLoader());
        formats = new ArrayList<>();
        source.readList(formats, String.class.getClassLoader());
        colors = new ArrayList<>();
        source.readList(colors, String.class.getClassLoader());
        layout = source.readString();
        loyalty = source.readString();
        altTitles = new ArrayList<>();
        source.readList(altTitles, String.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(frenchTitle);
        dest.writeString(type);
        dest.writeString(castingCost);
        dest.writeString(power);
        dest.writeString(toughness);
        dest.writeString(description);
        dest.writeList(publications);
        dest.writeList(formats);
        dest.writeList(colors);
        dest.writeString(layout);
        dest.writeString(loyalty);
        dest.writeList(altTitles);
    }

    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCastingCost() {
        return castingCost;
    }

    public void setCastingCost(String castingCost) {
        this.castingCost = castingCost;
    }

    public String getFrenchTitle() {
        return frenchTitle;
    }

    public void setFrenchTitle(String frenchTitle) {
        this.frenchTitle = frenchTitle;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getToughness() {
        return toughness;
    }

    public void setToughness(String toughness) {
        this.toughness = toughness;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(String loyalty) {
        this.loyalty = loyalty;
    }

    public List<String> getAltTitles() {
        return altTitles;
    }

    public void setAltTitles(List<String> altTitles) {
        this.altTitles = altTitles;
    }
}