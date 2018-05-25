package com.example.iit.androidnewsappspl2.InterfaceForGETAnnotaion;

import com.example.iit.androidnewsappspl2.ModelForSourcesAndNewsArticle.IconBetterIdea;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;



public interface IconBetterIdeaService {
    @GET
    Call<IconBetterIdea> getIconUrl(@Url String url);
}
