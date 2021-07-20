package list.user.listingapp.utils

import android.content.Context
import list.user.listingapp.BuildConfig
fun Context.isDebug(callback:(yes: Boolean)->Unit){
    if (BuildConfig.DEBUG){
        callback.invoke(true)
    }
}

fun Context.isNotDebug(callback:(yes: Boolean)->Unit){
    if (!BuildConfig.DEBUG){
        callback.invoke(true)
    }
}