package com.maghribpress.torrebook.network;


import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mlynx on 06/04/2018.
 */

public interface GetBooksDataService {

    @GET("books")
    Call<ResponseBody> books(@Query("page") int page, @Query("token") String token);

    @GET("findbook")
    Call<ResponseBody> findbook(@Query(value="q",encoded=true) String q, @Query("token") String token, @Query("langRestrict") String langRestrict );

    @GET("getbook")
    Call<ResponseBody> getbook(@Query("q") String q, @Query("token") String token, @Query("page") int page );

    @GET("auth/login/{provider}/callback")
    Call<ResponseBody> callback(@Path(value = "provider") String provider, @Query("token") String token);

    @POST("books")
    @FormUrlEncoded
    Call<ResponseBody> insertbook(@Field("googleid") String googleid, @Field("title") String title, @Field("pages") int pages, @Field("language") String language, @Field("coverURL") String coverUrl, @Field("isbn10") String isbn10, @Field("isbn13") String isbn13, @Field("releaseDate")String releaseDate,@Field("rating") float rating,@Field("ratingcount") int ratingcount,@Field("publisher") String publisher,@Field("description") String description , @Field("authors[]") List<String> authors, @Field("authorsphotos[]") List<String> authorsphotos,@Field("categories[]") List<String> categories, @Query("token") String token);

    @POST("auth/login")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("email") String emailvalue, @Field("password") String passwordvalue);

    @POST("auth/register")
    @FormUrlEncoded
    Call<ResponseBody> register(@Field("name") String namevalue, @Field("email") String emailvalue, @Field("password") String passwordvalue);
}
