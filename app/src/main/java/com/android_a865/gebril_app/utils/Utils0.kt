package com.android_a865.gebril_app.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.text.SimpleDateFormat
import java.util.Locale


fun Long.date(format: String = DATE_FORMATS[0]) : String {
    return SimpleDateFormat(format, Locale.getDefault()).format(this)
}

/** gets the image from the local device storage */
fun getTheImage(path: String): Bitmap? {
    return BitmapFactory.decodeFile(path)
}





