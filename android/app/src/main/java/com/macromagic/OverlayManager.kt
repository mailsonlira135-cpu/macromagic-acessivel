package com.macromagic.shizuku

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout

data class FloatingButtonConfig(
    var name: String = "AÇÃO PRINCIPAL",
    var x: Int = 100,
    var y: Int = 100,
    var size: Int = 150,
    var transparency: Float = 0.8f,
    var isPositionLocked: Boolean = false
)

class OverlayManager(private val context: Context) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val activeOverlays = mutableMapOf<String, View>()
    private var mainButtonConfig = FloatingButtonConfig()

    fun showMainActionButton() {
        val buttonId = "main_action"
        if (activeOverlays.containsKey(buttonId)) return

        val viewType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            mainButtonConfig.size,
            mainButtonConfig.size,
            viewType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = mainButtonConfig.x
            y = mainButtonConfig.y
        }

        val frameLayout = FrameLayout(context)
        val button = Button(context).apply {
            text = mainButtonConfig.name
            alpha = mainButtonConfig.transparency
            setBackgroundColor(android.graphics.Color.argb(200, 33, 150, 243))
        }

        button.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: android.view.MotionEvent): Boolean {
                if (mainButtonConfig.isPositionLocked) return false

                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    android.view.MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(frameLayout, params)
                        mainButtonConfig.x = params.x
                        mainButtonConfig.y = params.y
                        return true
                    }
                }
                return false
            }
        })

        frameLayout.addView(button)
        windowManager.addView(frameLayout, params)
        activeOverlays[buttonId] = frameLayout
    }

    fun togglePositionLock(locked: Boolean) {
        mainButtonConfig.isPositionLocked = locked
    }
}

