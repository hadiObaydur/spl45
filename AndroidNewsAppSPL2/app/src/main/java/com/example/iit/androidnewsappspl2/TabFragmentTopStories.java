package com.example.iit.androidnewsappspl2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iit.androidnewsappspl2.AdapterForNewsAndSource.ListNewsAdapter;
import com.example.iit.androidnewsappspl2.CommonForNewsAPIOrg.Common;
import com.example.iit.androidnewsappspl2.InterfaceForGETAnnotaion.NewsService;
import com.example.iit.androidnewsappspl2.ModelForSourcesAndNewsArticle.Article;
import com.example.iit.androidnewsappspl2.ModelForSourcesAndNewsArticle.News;
import com.example.iit.androidnewsappspl2.ModelForSourcesAndNewsArticle.Source;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.florent37.diagonallayout.DiagonalLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabFragmentTopStories extends Fragment {
    KenBurnsView kbv;
    DiagonalLayout diagonalLayout;
    AlertDialog dialog;
    NewsService mService;
    TextView top_author,top_title;
    SwipeRefreshLayout swipeRefreshLayout;

    String source="",sortBy="",webHotURL="";

    ListNewsAdapter adapter;
    RecyclerView lstNews;
    RecyclerView.LayoutManager layoutManager;



    private int position;
    private String category;
    private TextView content;

    private  String selectedLanguage;

    ArrayList<String>blockSourcesForAdapter=new ArrayList<String>();
    // private ImageView image;

    public static Fragment getInstance(int position) {
        TabFragmentTopStories f = new TabFragmentTopStories();
        Bundle args = new Bundle();
        // args.putInt("position", position);
       // args.putString("category", category);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get data from Argument

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,@Nullable Bundle savedInstanceState) {

        //category = getArguments().getString("category");

        return inflater.inflate(R.layout.activity_list_news, container, false);
    }

    @Override
    public void onViewCreated(View view , @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // Toast.makeText(getActivity(),category,Toast.LENGTH_LONG).show();
        //image = (ImageView) view.findViewById(R.id.image);
        //content = (TextView) view.findViewById(R.id.textView);
        setContentView(view);
    }

    private void setContentView(View view) {
        selectedLanguage=SingletonForUserInformation.getSingletonForUserInformation().getSelectedLanguage();
        blockSourcesForAdapter=SingletonForUserInformation.getSingletonForUserInformation().getBlockSourcesForAdapter();
        mService = Common.getNewsService();

        dialog = new SpotsDialog(getContext());

        //View
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNews(selectedLanguage,true);
            }
        });

        diagonalLayout = view.findViewById(R.id.diagonalLayout);
        diagonalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detail = new Intent(getContext(),DetailArticle.class);
                detail.putExtra("webURL",webHotURL);
                startActivity(detail);
            }
        });
        kbv = view.findViewById(R.id.top_image);
        top_author = view.findViewById(R.id.top_author);
        top_title = view.findViewById(R.id.top_title);

        lstNews = view.findViewById(R.id.lstNews);
        lstNews.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        lstNews.setLayoutManager(layoutManager);

        //Intent
        loadNews(selectedLanguage,true);
    }
    private void loadNews(String source,boolean isRefreshed) {
        if(!isRefreshed)
        {
            dialog.show();
            mService.getNewestArticles(Common.getAPIUrlll(source,sortBy,Common.API_KEY))
                    .enqueue(new Callback<News>() {
                        @Override
                        public void onResponse(Call<News> call, Response<News> response) {
                            dialog.dismiss();
                            List<Article> removeFristItem = response.body().getArticles();

                            //Get first article
//                            Picasso.with(getContext())
//                                    .load(response.body().getArticles().get(0).getUrlToImage())
//                                    .into(kbv);
//
//                            top_title.setText(response.body().getArticles().get(0).getTitle());
//                            top_author.setText(response.body().getArticles().get(0).getAuthor());
//
//                            webHotURL = response.body().getArticles().get(0).getUrl();


                            if(blockSourcesForAdapter.size()>0) {
                                int m=removeFristItem.size();
                                int n=blockSourcesForAdapter.size();
                                for (int i = 0; i < m; i++) {
                                    for(int j=0;j<n;j++) {
                                        if (removeFristItem.get(i).getSourceOfArticle().getName().equalsIgnoreCase(blockSourcesForAdapter.get(j))){
                                            removeFristItem.remove(i);
                                            m=m-1;
                                            i=i-1;
                                            break;
                                        }
//
                                    }
                                }
                            }

                            if(removeFristItem.get(0).getUrlToImage()!=null) {
                                Picasso.with(getContext())
                                        .load(removeFristItem.get(0).getUrlToImage())
                                        .into(kbv);
                            }
                            if(removeFristItem.get(0).getTitle()!=null)
                                   top_title.setText(removeFristItem.get(0).getTitle());
                            if(removeFristItem.get(0).getAuthor()!=null)
                                 top_author.setText(removeFristItem.get(0).getAuthor());

                            webHotURL = removeFristItem.get(0).getUrl();

                            //Load remain articles
                            //List<Article> removeFristItem = response.body().getArticles();
                            //Because we already load first item to show on Diagonal Layout
                            //So we need remove it
                            removeFristItem.remove(0);
                            adapter = new ListNewsAdapter(removeFristItem,getContext());
                            adapter.notifyDataSetChanged();
                            lstNews.setAdapter(adapter);

                        }

                        @Override
                        public void onFailure(Call<News> call, Throwable t) {

                        }
                    });
        }
        else
        {
            dialog.show();
            mService.getNewestArticles(Common.getAPIUrlll(source,sortBy,Common.API_KEY))
                    .enqueue(new Callback<News>() {
                        @Override
                        public void onResponse(Call<News> call, Response<News> response) {
                            dialog.dismiss();

                            List<Article>removeFristItem=response.body().getArticles();
                            //Get first article


                            //Get first article
//                            Picasso.with(getContext())
//                                    .load(response.body().getArticles().get(0).getUrlToImage())
//                                    .into(kbv);
//
//                            top_title.setText(response.body().getArticles().get(0).getTitle());
//                            top_author.setText(response.body().getArticles().get(0).getAuthor());
//
//                            webHotURL = response.body().getArticles().get(0).getUrl();


                            if(blockSourcesForAdapter.size()>0) {
                                int m=removeFristItem.size();
                                int n=blockSourcesForAdapter.size();
                                for (int i = 0; i < m; i++) {
                                    for(int j=0;j<n;j++) {
                                        if (removeFristItem.get(i).getSourceOfArticle().getName().equalsIgnoreCase(blockSourcesForAdapter.get(j))){
                                            removeFristItem.remove(i);
                                            m=m-1;
                                            i=i-1;
                                            break;
                                        }
//
                                    }
                                }
                            }
                            if(removeFristItem.get(0).getUrlToImage()!=null) {
                                Picasso.with(getContext())
                                        .load(removeFristItem.get(0).getUrlToImage())
                                        .into(kbv);
                            }
                            if(removeFristItem.get(0).getTitle()!=null)
                                top_title.setText(removeFristItem.get(0).getTitle());
                            if(removeFristItem.get(0).getAuthor()!=null)
                                top_author.setText(removeFristItem.get(0).getAuthor());
                            webHotURL = removeFristItem.get(0).getUrl();


                            removeFristItem.remove(0);
                            adapter = new ListNewsAdapter(removeFristItem,getContext());
                            adapter.notifyDataSetChanged();
                            lstNews.setAdapter(adapter);

                        }

                        @Override
                        public void onFailure(Call<News> call, Throwable t) {

                        }
                    });
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}