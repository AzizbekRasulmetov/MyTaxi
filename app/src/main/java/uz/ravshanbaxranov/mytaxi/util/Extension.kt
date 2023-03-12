package uz.ravshanbaxranov.mytaxi.util

import android.content.Context
import android.content.res.Configuration
import android.util.Log

fun showLog(msg: Any, tag: String = "TAGDF") {
    Log.d(tag, msg.toString())
}

fun isDarkTheme(context: Context): Boolean {
    return context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}