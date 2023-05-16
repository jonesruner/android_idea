package plus.jone.android_idea.base.utills

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


object JsonUtils {
    private val TAG = "JsonUtils"
    private val gson by lazy {
        Gson()
    }
    private val divider_symbol = arrayListOf('{', '}', ',', '[', ']')
    fun prettyJson(string: String): String {
        var str = StringBuilder()
        string.forEach {
            if (divider_symbol.indexOf(it) != -1) {
                str.append(it).append('\n').append('\t')
            } else {
                str.append(it)
            }
        }
        return str.toString()
    }

    fun <T> convertJsonToObj(string: String, clz: Class<T>): T? {
        try{
            val json = gson.fromJson(string, clz)
            return json
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
        return null
    }

    fun <T> convertJsonToObjOrArray(string: String, tp: Type): List<T> {
        return gson.fromJson(string,tp)
    }


    fun  convertObjToString(any: Any): String? {
        return gson.toJson(any)
    }
}