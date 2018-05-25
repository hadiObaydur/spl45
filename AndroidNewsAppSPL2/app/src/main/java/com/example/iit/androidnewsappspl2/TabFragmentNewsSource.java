package com.example.iit.androidnewsappspl2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iit.androidnewsappspl2.AdapterForNewsAndSource.ListSourceAdapter;
import com.example.iit.androidnewsappspl2.CommonForNewsAPIOrg.Common;
import com.example.iit.androidnewsappspl2.InterfaceForGETAnnotaion.NewsService;
import com.example.iit.androidnewsappspl2.ModelForSourcesAndNewsArticle.Source;
import com.example.iit.androidnewsappspl2.ModelForSourcesAndNewsArticle.WebSite;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabFragmentNewsSource extends Fragment {
    RecyclerView listWebsite;
    RecyclerView.LayoutManager layoutManager;
    NewsService mService;
    ListSourceAdapter adapter;
    AlertDialog dialog;
    SwipeRefreshLayout swipeLayout;


    private int position;
    private String category;
    private String selectedCountry;
    ArrayList<String>blockSourcesForAdapter=new ArrayList<String>();
   // private TextView content;
    // private ImageView image;

    public static Fragment getInstance(int position) {
        TabFragmentNewsSource f = new TabFragmentNewsSource();
        Bundle args = new Bundle();
         args.putInt("position", position);
        //args.putString("category", category);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position=getArguments().getInt("position");
        //get data from Argument

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,@Nullable Bundle savedInstanceState) {

       // category = getArguments().getString("category");

        return inflater.inflate(R.layout.list_of_sources_layout, container, false);
    }

    @Override
    public void onViewCreated(View view , @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Toast.makeText(getActivity(),category,Toast.LENGTH_LONG).show();
        //image = (ImageView) view.findViewById(R.id.image);
        //content = (TextView) view.findViewById(R.id.textView);
        selectedCountry=SingletonForUserInformation.getSingletonForUserInformation().getSelectedLanguage();
        blockSourcesForAdapter=SingletonForUserInformation.getSingletonForUserInformation().getBlockSourcesForAdapter();
        // blockSourcesForAdapter because id null thake but source name null thake na ..blocksources adapter name er moto
        Paper.init(getContext());

        //Init Service
        mService = Common.getNewsService();

        //Init View
        swipeLayout = view.findViewById(R.id.swipeRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadWebsiteSource(true);

            }
        });

        listWebsite =view.findViewById(R.id.list_source);
        listWebsite.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        listWebsite.setLayoutManager(layoutManager);

        dialog = new SpotsDialog(getActivity());

        loadWebsiteSource(true);
        //Toast.makeText(getContext(),""+SingletonForUserInformation.getSingletonForUserInformation().getBlockSourcesForAdapter().get(0),Toast.LENGTH_LONG).show();
    }
    private void loadWebsiteSource(boolean isRefreshed) {
        if(!isRefreshed)
        {

            String cache = Paper.book().read("cache");
            if(cache != null && !cache.isEmpty() && !cache.equals("null")) // If have cache
            {
                WebSite website = new Gson().fromJson(cache,WebSite.class); // Convert cache from Json to Object
                List<Source>listOfSources=website.getSources();
                adapter = new ListSourceAdapter(getContext(),listOfSources);
                adapter.notifyDataSetChanged();
                listWebsite.setAdapter(adapter);
            }
            else // If not have cache
            {
                dialog.show();
                //Fetch new data
                mService.getSources(Common.getSourcesAPIUrl(selectedCountry)).enqueue(new Callback<WebSite>() {
                    @Override
                    public void onResponse(Call<WebSite> call, Response<WebSite> response) {
                        List<Source>listOfSources=response.body().getSources();
                        if(blockSourcesForAdapter.size()>0) {
                            int m=listOfSources.size();
                            int n=blockSourcesForAdapter.size();
                            for (int i = 0; i < m; i++) {
                                for(int j=0;j<n;j++) {
                                    if (listOfSources.get(i).getName().equalsIgnoreCase(blockSourcesForAdapter.get(j))){
                                        listOfSources.remove(i);
                                        m=m-1;
                                        i=i-1;
                                        break;
                                    }
//
                                }
                            }
                        }
                        adapter  = new ListSourceAdapter(getContext(),listOfSources);
                        adapter.notifyDataSetChanged();
                        listWebsite.setAdapter(adapter);

                        //Save to cache
                        Paper.book().write("cache",new Gson().toJson(response.body()));

                        dialog.dismiss();

                    }

                    @Override
                    public void onFailure(Call<WebSite> call, Throwable t) {

                    }
                });
            }
        }
        else // If from Swipe to Refresh
        {

            swipeLayout.setRefreshing(true);
            //Fetch new data
            mService.getSources(Common.getSourcesAPIUrl(selectedCountry)).enqueue(new Callback<WebSite>() {
                @Override
                public void onResponse(Call<WebSite> call, Response<WebSite> response) {
                    List<Source>listOfSources=response.body().getSources();
                    if(blockSourcesForAdapter.size()>0) {
                        int m=listOfSources.size();
                        int n=blockSourcesForAdapter.size();
                        for (int i = 0; i < m; i++) {
                            for(int j=0;j<n;j++) {
                                if (listOfSources.get(i).getName().equalsIgnoreCase(blockSourcesForAdapter.get(j))){
                                   listOfSources.remove(i);
                                    m=m-1;
                                    i=i-1;
                                    break;
                                 }
//
                               }
                             }
                    }
                    adapter  = new ListSourceAdapter(getContext(),listOfSources);
                    adapter.notifyDataSetChanged();
                    listWebsite.setAdapter(adapter);

                    //Save to cache
                    Paper.book().write("cache",new Gson().toJson(response.body()));

                    //Dismiss refresh progressring
                    swipeLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<WebSite> call, Throwable t) {

                }
            });

        }
    }
}