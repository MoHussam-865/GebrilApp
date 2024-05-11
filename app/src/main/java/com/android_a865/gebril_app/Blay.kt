package com.android_a865.gebril_app

import com.android_a865.gebril_app.data.domain.Message
import com.google.gson.Gson

class Blay {

    fun receive(message: String): Message = Gson().fromJson(message, Message::class.java)

    fun send(message: Message): String = Gson().toJson(message)

}