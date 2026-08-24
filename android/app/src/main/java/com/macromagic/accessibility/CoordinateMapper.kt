package com.macromagic.accessibility

import android.graphics.Point
import android.util.DisplayMetrics
import kotlin.math.roundToInt

/**
 * Converts logical coordinates into physical display pixels while keeping
 * gesture endpoints inside the active display bounds.
 */
class CoordinateMapper {
    data class ScreenBounds(val widthPx: Int, val heightPx: Int) {
        init {
            require(widthPx > 0) { "widthPx must be positive" }
            require(heightPx > 0) { "heightPx must be positive" }
        }
    }

    data class Coordinate(val x: Float, val y: Float)

    fun fromDp(xDp: Float, yDp: Float, metrics: DisplayMetrics): Coordinate {
        require(xDp.isFinite() && yDp.isFinite()) { "DP coordinates must be finite" }
        return Coordinate(
            x = xDp * metrics.density,
            y = yDp * metrics.density,
        )
    }

    fun fromNormalized(
        x: Float,
        y: Float,
        screen: ScreenBounds,
    ): Coordinate {
        require(x in 0f..1f && y in 0f..1f) {
            "Normalized coordinates must be between 0 and 1."
        }
        return Coordinate(
            x = x * (screen.widthPx - 1),
            y = y * (screen.heightPx - 1),
        )
    }

    fun clampToScreen(x: Float, y: Float, screen: ScreenBounds): Coordinate =
        Coordinate(
            x = x.coerceIn(0f, (screen.widthPx - 1).toFloat()),
            y = y.coerceIn(0f, (screen.heightPx - 1).toFloat()),
        )

    fun round(coordinate: Coordinate): Point =
        Point(coordinate.x.roundToInt(), coordinate.y.roundToInt())
}