package fr.gstraymond.magicsearch.model.response;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class MagicCard implements Parcelable {
	private String title;
	private String frenchTitle;
	private String type;
	private String castingCost;
	private String power;
	private String toughness;
	private String description;
	private List<Publication> publications;

	public static final Parcelable.Creator<MagicCard> CREATOR = new Parcelable.Creator<MagicCard>() {
		@Override
		public MagicCard createFromParcel(Parcel source) {
			return new MagicCard(source);
		}

		@Override
		public MagicCard[] newArray(int size) {
			return new MagicCard[size];
		}
	};

	public MagicCard() {
	}

	public MagicCard(Parcel source) {
		title = source.readString();
		frenchTitle = source.readString();
		type = source.readString();
		castingCost = source.readString();
		power = source.readString();
		toughness = source.readString();
		description = source.readString();
		publications = new ArrayList<Publication>();
		source.readList(publications, Publication.class.getClassLoader());
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
}
