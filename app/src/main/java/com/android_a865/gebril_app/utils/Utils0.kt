package com.android_a865.gebril_app.utils

import java.text.SimpleDateFormat
import java.util.*


fun Long.date(format: String = DATE_FORMATS[0]) : String {
    return SimpleDateFormat(format, Locale.getDefault()).format(this)
}


