package nl.teun.dpi.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun Any.toJson() = Gson().toJson(this)!!
inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
fun String.replaceLast(find: String, replace: String): String {
    val lastIndex = this.lastIndexOf(find)

    if (lastIndex == -1) {
        return this
    }

    val beginString = this.substring(0, lastIndex)
    val endString = this.substring(lastIndex + find.length)

    return beginString + replace + endString
}
val debug = false
fun printDebug(str:String) {
    if (debug) {
        println(str)
    }
}