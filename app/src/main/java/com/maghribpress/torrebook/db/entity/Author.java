package com.maghribpress.torrebook.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "authors",indices = {@Index(value = {"name"},
        unique = true)})
public class Author implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String pictureURL;
    private long created_at;

    public Author() {
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    protected Author(Parcel in) {
        id = in.readInt();
        name = in.readString();
        pictureURL = in.readString();
        created_at = in.readLong();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);;
        dest.writeString(name);;
        dest.writeString(pictureURL);
        dest.writeLong(created_at);
    }
    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };
}
