package com.morato.crypto.apiManager

import com.google.gson.JsonObject
import com.morato.crypto.model.API_KEY
import com.morato.crypto.model.ChartData
import com.morato.crypto.model.CoinsData
import com.morato.crypto.model.NewsData
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Headers(API_KEY)
    @GET("v2/news/")
    fun getTopNews(
        @Query("sortOrder") sortOrder : String = "popular"
    ) : Call<NewsData>

    @Headers(API_KEY)
    @GET("top/totalvolfull")
    fun getTopCoins(
        @Query("tsym") toSymbol : String = "USD",
        @Query("limit") limitData : Int = 10
    ) : Call<CoinsData>

    @Headers(API_KEY)
    @GET("{period}")
    fun getChartData(
        @Path("period") period : String ,
        @Query("fsym") fromSymbol : String ,
        @Query("limit") limit : Int ,
        @Query("aggregate") aggregate : Int ,
        @Query("tsym") toSymbol : String = "USD"
    ) : Call<ChartData>
}