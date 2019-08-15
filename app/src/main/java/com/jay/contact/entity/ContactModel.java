package com.jay.contact.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactModel implements Parcelable {
    private long id;
    private String name;
    private String phoneNum;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phoneNum);
    }

    public ContactModel() {
    }

    public ContactModel(long id) {
        this.id = id;
    }

    protected ContactModel(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.phoneNum = in.readString();
    }

    public static final Parcelable.Creator<ContactModel> CREATOR = new Parcelable.Creator<ContactModel>() {
        @Override
        public ContactModel createFromParcel(Parcel source) {
            return new ContactModel(source);
        }

        @Override
        public ContactModel[] newArray(int size) {
            return new ContactModel[size];
        }
    };
}
