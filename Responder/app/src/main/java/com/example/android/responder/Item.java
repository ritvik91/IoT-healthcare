package com.example.android.responder;

/**
 * Created by GrayShadow on 4/12/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kyle.jablonski on 10/6/15.
 */
public class Item implements Parcelable{



    public String title;
    public String description;
    public boolean isExpanded;


    public Item(){}

    public Item(Parcel in){
        title = in.readString();
        description = in.readString();
        isExpanded = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(isExpanded ? 1 : 0);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>(){
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
