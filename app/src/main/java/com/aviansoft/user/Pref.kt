package com.aviansoft.caronphone.Utils

import android.content.Context
import android.content.SharedPreferences

class Pref {

    constructor(context: Context, name: String) {
        pref = context.getSharedPreferences(name, 0)
        editor = pref.edit()
    }


    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor


    fun setPref(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun setPref(key: String, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    fun getPrefString(key: String): String {
        return pref.getString(key, "")!!
    }

    fun getPrefInt(key: String): Int {
        return pref.getInt(key, 0)
    }


}