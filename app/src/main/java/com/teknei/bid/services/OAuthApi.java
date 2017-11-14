package com.teknei.bid.services;

import com.teknei.bid.response.OAuthAccessToken;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by marojas on 13/10/2017.
 */

public interface OAuthApi {

    @Headers("Authorization: Basic YmlkOmJpZA==")
    @POST("/oauth/token?grant_type=client_credentials")
    public Call<OAuthAccessToken> getAccessTokenByClientCredentials();

    @Headers("Authorization: Basic YmlkOmJpZA==")
    @POST("/oauth/token?grant_type=password")
    public Call<OAuthAccessToken> getAccessTokenByPassword(
            @Query("username") String username,
            @Query("password") String password
    );

    @GET("/me")
    public Call<Object> getOwnerInfo(@Query("access_token") String accessToken);

}
