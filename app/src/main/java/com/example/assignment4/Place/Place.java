package com.example.assignment4.Place;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    public String	title;
    public String	thumb_url;
    public String   time;
    public String[]   type;
    public String   open_hour;
    public String   address;
    public double   lat;
    public double   lng;


    public Place(){

    }

    public Place(Parcel in) {
        // This order must match the order in writeToParcel()
           title= in.readString();
            address=in.readString();
        time=in.readString();


        // Continue doing this for the rest of your member data
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(address);
        out.writeString(time);
    }
    // Just cut and paste this for now
    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}