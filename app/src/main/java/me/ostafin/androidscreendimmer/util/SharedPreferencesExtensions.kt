package me.ostafin.androidscreendimmer.util

import android.annotation.SuppressLint
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private inline fun <T> SharedPreferences.delegate(
    defaultValue: T, key: String? = null,
    crossinline getter: SharedPreferences.(String?, T) -> T?,
    crossinline setter: SharedPreferences.Editor.(String?, T) -> SharedPreferences.Editor?
): ReadWriteProperty<Any, T> =
    object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T =
            getter(key ?: property.name, defaultValue)!!

        // commit() instead of apply() because of possible dependency injection right after saving
        @SuppressLint("ApplySharedPref")
        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            edit().setter(key ?: property.name, value)?.commit()
        }
    }

fun SharedPreferences.int(def: Int = 0, key: String? = null): ReadWriteProperty<Any, Int> =
    delegate(def, key, SharedPreferences::getInt, SharedPreferences.Editor::putInt)

fun SharedPreferences.long(def: Long = 0, key: String? = null): ReadWriteProperty<Any, Long> =
    delegate(def, key, SharedPreferences::getLong, SharedPreferences.Editor::putLong)

fun SharedPreferences.float(def: Float = 0f, key: String? = null): ReadWriteProperty<Any, Float> =
    delegate(def, key, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)

fun SharedPreferences.boolean(
    def: Boolean = false,
    key: String? = null
): ReadWriteProperty<Any, Boolean> =
    delegate(def, key, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)

fun SharedPreferences.stringSet(
    def: Set<String> = emptySet(),
    key: String? = null
): ReadWriteProperty<Any, Set<String>> =
    delegate(def, key, SharedPreferences::getStringSet, SharedPreferences.Editor::putStringSet)

fun SharedPreferences.string(
    def: String = "",
    key: String? = null
): ReadWriteProperty<Any, String> =
    delegate(def, key, SharedPreferences::getString, SharedPreferences.Editor::putString)

//inline fun <reified K, reified V> SharedPreferences.map(
//    def: Map<K, V> = emptyMap(),
//    key: String? = null,
//    gson: Gson
//): ReadWriteProperty<Any, Map<K, V>> =
//    object : ReadWriteProperty<Any, Map<K, V>> {
//        override fun getValue(thisRef: Any, property: KProperty<*>): Map<K, V> {
//            val jsonValue = getString(key, "") ?: ""
//            return if (jsonValue.isNotEmpty()) {
//                val mapType =
//                    TypeToken.getParameterized(Map::class.java, K::class.java, V::class.java).type
//
//                val result = gson.fromJson<Map<K, V>>(jsonValue, mapType)
//                result as Map<K, V>
//            } else {
//                def
//            }
//        }
//
//        // commit() instead of apply() because of possible dependency injection right after saving
//        @SuppressLint("ApplySharedPref")
//        override fun setValue(thisRef: Any, property: KProperty<*>, value: Map<K, V>) {
//            val mapType =
//                TypeToken.getParameterized(Map::class.java, K::class.java, V::class.java).type
//
//            val jsonValue = gson.toJson(value, mapType)
//            edit().putString(key, jsonValue).commit()
//        }
//    }