package me.ostafin.androidscreendimmer.util

import com.jakewharton.rxrelay2.Relay

fun Relay<Unit>.accept() = accept(Unit)