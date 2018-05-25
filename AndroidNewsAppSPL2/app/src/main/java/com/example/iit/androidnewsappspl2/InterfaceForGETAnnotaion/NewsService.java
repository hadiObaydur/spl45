package com.example.iit.androidnewsappspl2.InterfaceForGETAnnotaion;

import com.example.iit.androidnewsappspl2.ModelForSourcesAndNewsArticle.News;
import com.example.iit.androidnewsappspl2.ModelForSourcesAndNewsArticle.WebSite;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface NewsService {
   // @GET("v2/sources?language=en&category=sports&apiKey="+Common.API_KEY)
    @GET
    Call<WebSite> getSources(@Url String url);

    @GET
    Call<News> getNewestArticles(@Url String url);

}
