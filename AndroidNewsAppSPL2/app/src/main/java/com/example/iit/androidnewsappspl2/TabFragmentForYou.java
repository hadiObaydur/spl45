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

import com.example.iit.androidnewsappspl2.InterfaceForGETAnnotaion.NewsService;

import java.util.ArrayList;

public class TabFragmentForYou extends Fragment {
    RecyclerView listWebsite;
    RecyclerView.LayoutManager layoutManager;
    NewsService mService;
    ForYouAdapter adapter;
    AlertDialog dialog;
    SwipeRefreshLayout swipeLayout;

    private int position;
    ArrayList<String>preferSources=new ArrayList<String>();
    ArrayList<String>preferSourcesForAdapter=new ArrayList<String>();
    ArrayList<String>blockSourcesForAdapter=new ArrayList<String>();
   // private String category;
    //private String selectedCountry;
    // private TextView content;
    // private ImageView image;

    public static Fragment getInstance(int position) {
        TabFragmentForYou f = new TabFragmentForYou();
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

        return inflater.inflate(R.layout.list_of_prefersources_layout, container, false);
    }

    @Override
    public void onViewCreated(View view , @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // Toast.makeText(getActivity(),""+position,Toast.LENGTH_LONG).show();
        //image = (ImageView) view.findViewById(R.id.image);
        //content = (TextView) view.findViewById(R.id.textView);
        preferSources=SingletonForUserInformation.getSingletonForUserInformation().getPreferSources();
        preferSourcesForAdapter=SingletonForUserInformation.getSingletonForUserInformation().getPreferSourcesForAdapter();
        blockSourcesForAdapter=SingletonForUserInformation.getSingletonForUserInformation().getBlockSourcesForAdapter();
        //Paper.init(getContext());

        //Init Service
        //mService = Common.getNewsService();

        //Init View
       // swipeLayout = getActivity().findViewById(R.id.swipeRefresh);
//        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadForYouSource();
//
//            }
//        });

        if(blockSourcesForAdapter.size()>0&&preferSourcesForAdapter.size()>0) {
          //  int m=preferSourcesForAdapter.size();
            int n=blockSourcesForAdapter.size();
            for (int i = 0; i < n; i++) {
               if(preferSourcesForAdapter.contains(blockSourcesForAdapter.get(i))){

                   int pos=preferSourcesForAdapter.indexOf(blockSourcesForAdapter.get(i));
                   preferSourcesForAdapter.remove(blockSourcesForAdapter.get(i));
                   preferSources.remove(pos);
                   i--;
                   n--;
               }
            }
        }

       listWebsite =view.findViewById(R.id.list_prefersource);
        listWebsite.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        listWebsite.setLayoutManager(layoutManager);
        adapter  = new ForYouAdapter(getContext(), preferSources,preferSourcesForAdapter);
        adapter.notifyDataSetChanged();
        listWebsite.setAdapter(adapter);

        //dialog = new SpotsDialog(getActivity());

       // loadForYouSource();
        //Toast.makeText(getContext(),""+SingletonForUserInformation.getSingletonForUserInformation().getBlockSourcesForAdapter().get(0),Toast.LENGTH_LONG).show();
    }
    private void loadForYouSource() {


                    adapter  = new ForYouAdapter(getContext(),preferSources,preferSourcesForAdapter);
                    adapter.notifyDataSetChanged();
                    listWebsite.setAdapter(adapter);



             //swipeLayout.setRefreshing(false);


    }
}