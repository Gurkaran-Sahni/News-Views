package com.example.android.newsviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<News>> {

    public static String SAMPLE_JSON_RESPONSE;
    public NewsAdapter adapter;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_news, container, false);

        SAMPLE_JSON_RESPONSE = "http://content.guardianapis.com/search";

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            ListView listView = rootView.findViewById(R.id.news_list);
            adapter = new NewsAdapter(getActivity(), new ArrayList<News>());
            listView.setAdapter(adapter);

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, this);
            Log.v("NewsFragment", "initLoader");
        }
        else {
            rootView.findViewById(R.id.loading_spinner_news).setVisibility(View.GONE);
            TextView mEmpty = rootView.findViewById(R.id.empty_news);
            mEmpty.setText("Please, make sure you are connected to internet");
        }

        return rootView;
    }

    @Override
    public Loader<ArrayList<News>> onCreateLoader(int i, Bundle bundle) {
        Log.v("NewsFragment","onCreateLoader");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        String orderBy = sharedPrefs.getString(
                "order by",
                "newest"
        );

        String country = sharedPrefs.getString(
                "country",
                "us"
        );

        String pageSize = sharedPrefs.getString(
                "page size",
                "10"
        );


        Uri baseUri = Uri.parse(SAMPLE_JSON_RESPONSE);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", country);
        uriBuilder.appendQueryParameter("section", "world");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail,trailText");
        uriBuilder.appendQueryParameter("page-size", pageSize);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", "9a2079fe-35fe-47aa-8770-2b98fbb5ea08");

        Log.v("NewsFragment",uriBuilder.toString());
        return new NewsLoader(getContext(),uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<News>> loader, ArrayList<News> news) {
        adapter.clear();
        Log.v("NewsFragment","onLoadFinished");
        if(news == null || news.isEmpty())
        {
            TextView mEmpty = getActivity().findViewById(R.id.empty_news);
            getActivity().findViewById(R.id.loading_spinner_news).setVisibility(View.GONE);
            mEmpty.setText("Sorry, no World News found :(");
            return;
        }
        else
        {
            adapter.clear();
            getActivity().findViewById(R.id.loading_spinner_news).setVisibility(View.GONE);
            adapter.addAll(news);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<News>> loader)
    {
        adapter.clear();
        Log.v("NewsFragment","onLoaderReset");
    }
}