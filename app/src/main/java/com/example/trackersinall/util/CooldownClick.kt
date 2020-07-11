package com.example.trackersinall.util

import android.os.SystemClock
import android.view.View
import java.util.*

/**
 * Classe que impede usuario de clicar diversas vezes seguidas em um mesmo botão
 */
private const val minimumInterval = 1000L

class CooldownClick (private val onClick: (view: View) -> Unit) : View.OnClickListener {
    private val lastClickMap: MutableMap<View, Long> = WeakHashMap()

    override fun onClick(clickedView: View) {
        val previousClickTimestamp = lastClickMap[clickedView]
        val currentTimestamp = SystemClock.uptimeMillis()

        lastClickMap[clickedView] = currentTimestamp
        if (previousClickTimestamp == null || currentTimestamp - previousClickTimestamp.toLong() > minimumInterval) {
            onClick.invoke(clickedView)
        }
    }
}