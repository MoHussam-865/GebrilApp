package com.android_a865.gebril_app.external_api

import com.android_a865.gebril_app.data.domain.Message
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ItemsApi {

    @POST("/items/")
    suspend fun getItems(@Body message: Message): Response<Message>

}