package me.ostafin.androidscreendimmer.ui.main.model

sealed class OverlayState {
    data class Visible(val alphaValue: Float) : OverlayState()
    object Hidden : OverlayState()
}