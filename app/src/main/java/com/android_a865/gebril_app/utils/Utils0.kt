package com.android_a865.gebril_app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


fun Long.date(format: String = DATE_FORMATS[0]) : String {
    return SimpleDateFormat(format, Locale.getDefault()).format(this)
}


fun String.asFileExist(context: Context): Boolean {
    return File(context.filesDir, this).exists()
}






