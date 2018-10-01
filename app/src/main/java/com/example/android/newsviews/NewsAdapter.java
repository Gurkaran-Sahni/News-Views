package com.example.android.newsviews;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News>{

    public NewsAdapter(Activity context, ArrayList<News>news){
        super(context,0,news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View ListView = convertView;

        if(ListView == null){
            ListView= LayoutInflater.from(getContext()).inflate(R.layout.news_layout,parent,false);
        }

        final News currentItem = getItem(position);

        TextView newsTitle = ListView.findViewById(R.id.news_title);
        newsTitle.setText(currentItem.getmTitle());

        TextView newsCategory = ListView.findViewById(R.id.news_category);
        newsCategory.setText(currentItem.getmCategory());

        TextView newsDatetime = ListView.findViewById(R.id.news_datetime);
        newsDatetime.setText(currentItem.getmDateTime());

        TextView newsAuthor = ListView.findViewById(R.id.news_author);
        newsAuthor.setText(currentItem.getmAuthor());

        ImageView newsThumbnail = ListView.findViewById(R.id.news_thumbnail);
        newsThumbnail.setImageBitmap(currentItem.getmImage());

        TextView newsDescription = ListView.findViewById(R.id.news_description);
        newsDescription.setText(currentItem.getmDescripton());

        return ListView;
    }
}
