package com.android_a865.gebril_app.external_api

import com.android_a865.gebril_app.data.domain.Message
import com.android_a865.gebril_app.data.entities.Invoice
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ItemsApi {

    @POST("api/Products/update")
    suspend fun getItems(@Body lastUpdate: Int): Response<Message>

    @POST("api/products/order")
    suspend fun sendOrder(@Body invoice: Invoice): Response<String>

    @GET("api/Products/download")
    @Streaming
    suspend fun downloadImage(@Query("imageName") imageName: String): Call<ResponseBody>

}