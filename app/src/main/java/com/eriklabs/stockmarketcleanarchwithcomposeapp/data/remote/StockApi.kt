package com.eriklabs.stockmarketcleanarchwithcomposeapp.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    companion object {
        const val API_KEY = "3UV36E2TVXSPSSPN"
        const val BASE_URL = "https://www.alphavantage.co"
    }

    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(
        @Query("apikey") apiKey: String
    ): ResponseBody

}