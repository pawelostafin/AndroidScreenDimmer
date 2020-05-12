package me.ostafin.androidscreendimmer.util

import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

fun AppCompatSeekBar.setOnSeekBarChangeListener(
    onProgressChanged: ((seekBar: SeekBar, progress: Int, fromUser: Boolean) -> Unit)? = null,
    onStartTrackingTouch: ((seekBar: SeekBar) -> Unit)? = null,
    onStopTrackingTouch: ((seekBar: SeekBar) -> Unit)? = null
) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            onProgressChanged?.invoke(seekBar, progress, fromUser)

        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            onStartTrackingTouch?.invoke(seekBar)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            onStopTrackingTouch?.invoke(seekBar)
        }
    })
}