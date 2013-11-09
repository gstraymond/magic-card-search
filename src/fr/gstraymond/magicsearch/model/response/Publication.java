package fr.gstraymond.magicsearch.model.response;

import android.os.Parcel;
import android.os.Parcelable;

public class Publication implements Parcelable {

	String rarity;
	String edition;
	String editionCode;
	String image;

	public static final Parcelable.Creator<Publication> CREATOR = new Parcelable.Creator<Publication>() {
		@Override
		public Publication createFromParcel(Parcel source) {
			return new Publication(source);
		}

		@Override
		public Publication[] newArray(int size) {
			return new Publication[size];
		}
	};

	public Publication() {
	}

	public Publication(Parcel source) {
		rarity = source.readString();
		edition = source.readString();
		editionCode = source.readString();
		image = source.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(rarity);
		dest.writeString(edition);
		dest.writeString(editionCode);
		dest.writeString(image);
	}
	
	public String getRarity() {
		return rarity;
	}
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
	public String getEdition() {
		return edition;
	}
	public void setEdition(String edition) {
		this.edition = edition;
	}
	public String getEditionCode() {
		return editionCode;
	}
	public void setEditionCode(String editionCode) {
		this.editionCode = editionCode;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}
