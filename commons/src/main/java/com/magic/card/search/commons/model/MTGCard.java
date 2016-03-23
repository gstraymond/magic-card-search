package com.magic.card.search.commons.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MTGCard implements Parcelable {

    public String title;
    public String collectorNumber;
    public String description;
    public String type;
    public String castingCost;

    public MTGCard() {
        // jackson constructor
    }

    protected MTGCard(Parcel in) {
        title = in.readString();
        collectorNumber = in.readString();
        description = in.readString();
        type = in.readString();
        castingCost = in.readString();
    }

    public static final Creator<MTGCard> CREATOR = new Creator<MTGCard>() {
        @Override
        public MTGCard createFromParcel(Parcel in) {
            return new MTGCard(in);
        }

        @Override
        public MTGCard[] newArray(int size) {
            return new MTGCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(collectorNumber);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeString(castingCost);
    }
}
