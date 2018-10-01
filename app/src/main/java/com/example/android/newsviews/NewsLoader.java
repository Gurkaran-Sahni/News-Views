package com.example.android.newsviews;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class NewsLoader extends AsyncTaskLoader<ArrayList<News>> {

    private String mUrl;

    public NewsLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.v("NewsLoader","onStartLoading");
    }

    @Override
    public ArrayList<News> loadInBackground() {
        if(mUrl==null){
            return null;
        }
        Log.v("NewsLoader","loadInBackground");

        ArrayList<News> news = QueryUtils.fetchNewsData(mUrl);
        return news;
    }
}
