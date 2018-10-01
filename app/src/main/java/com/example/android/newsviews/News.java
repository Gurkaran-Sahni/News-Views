package com.example.android.newsviews;

import android.graphics.Bitmap;

public class News {
    private String mTitle;
    private String mCategory;
    private String mDateTime;
    private String mDescripton;
    private String mAuthor;
    private Bitmap mImage;
    private int mTempImage;

    public News(String title, String category, String datetime, String description, String author, Bitmap image,int tempimage){
        mTitle = title;
        mCategory = category;
        mDateTime = datetime;
        mDescripton = description;
        mAuthor = author;
        mImage = image;
        mTempImage = tempimage;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmCategory() {
        return mCategory;
    }

    public String getmDateTime() {
        return mDateTime;
    }

    public String getmDescripton() {
        return mDescripton;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public Bitmap getmImage() {
        return mImage;
    }

    public int getmTempImage() {
        return mTempImage;
    }
}
