package com.macromagic.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import kotlin.math.abs
import kotlin.math.max

/**
 * Executes explicit, user-configured touch gestures through Android's
 * accessibility API. It does not use hidden input APIs or bypass app controls.
 *
 * Callers must guard this class with [Build.VERSION.SDK_INT] >=
 * [Build.VERSION_CODES.N], because dispatchGesture was added in API 24.
 */
class MacroExecutionEngine(
    private val accessibilityService: AccessibilityService,
    private val coordinateMapper: CoordinateMapper = CoordinateMapper(),
) {
    data class DragRequest(
        val startX: Float,
        val startY: Float,
        val offsetY: Float,
        val steps: Int = DEFAULT_STEPS,
        val durationPerStepMs: Long = DEFAULT_DURATION_PER_STEP_MS,
    )

    sealed interface Result {
        data object Dispatched : Result
        data class Rejected(val reason: String) : Result
        data object FailedToDispatch : Result
    }

    /**
     * Simulates one continuous vertical finger drag. Positive offsetY moves
     * downward; negative offsetY moves upward. The path is a single stroke
     * with interpolated points, so the gesture remains continuous.
     */
    fun executeVerticalDrag(
        request: DragRequest,
        screen: CoordinateMapper.ScreenBounds,
        onFinished: (Result) -> Unit = {},
    ): Result {
        val validation = validate(request, screen)
        if (validation != null) {
            val result = Result.Rejected(validation)
            onFinished(result)
            return result
        }

        val path = Path().apply {
            moveTo(request.startX, request.startY)
            for (step in 1..request.steps) {
                val progress = step.toFloat() / request.steps
                lineTo(request.startX, request.startY + request.offsetY * progress)
            }
        }
        val strokeDurationMs = max(
            MIN_STROKE_DURATION_MS,
            request.steps.toLong() * request.durationPerStepMs,
        )
        val gesture = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(
                    path,
                    0L,
                    strokeDurationMs,
                ),
            )
            .build()

        val dispatched = accessibilityService.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    onFinished(Result.Dispatched)
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    onFinished(Result.FailedToDispatch)
                }
            },
            null,
        )
        return if (dispatched) Result.Dispatched else Result.FailedToDispatch
    }

    private fun validate(
        request: DragRequest,
        screen: CoordinateMapper.ScreenBounds,
    ): String? {
        if (!request.startX.isFinite() || !request.startY.isFinite() ||
            !request.offsetY.isFinite()
        ) {
            return "Coordenadas e offsetY devem ser números finitos."
        }
        if (request.steps !in MIN_STEPS..MAX_STEPS) {
            return "steps deve estar entre $MIN_STEPS e $MAX_STEPS."
        }
        if (request.durationPerStepMs !in MIN_DURATION_PER_STEP_MS..MAX_DURATION_PER_STEP_MS) {
            return "durationPerStepMs está fora do intervalo permitido."
        }
        if (abs(request.offsetY) < MIN_OFFSET_PX) {
            return "offsetY deve produzir um deslocamento visível."
        }

        val start = coordinateMapper.clampToScreen(request.startX, request.startY, screen)
        val end = coordinateMapper.clampToScreen(
            request.startX,
            request.startY + request.offsetY,
            screen,
        )
        if (start.x != request.startX || start.y != request.startY) {
            return "O ponto inicial está fora dos limites da tela."
        }
        if (end.y != request.startY + request.offsetY) {
            return "O deslocamento ultrapassa os limites da tela."
        }
        return null
    }

    companion object {
        const val DEFAULT_STEPS = 12
        const val DEFAULT_DURATION_PER_STEP_MS = 16L
        private const val MIN_STEPS = 2
        private const val MAX_STEPS = 120
        private const val MIN_OFFSET_PX = 1f
        private const val MIN_DURATION_PER_STEP_MS = 8L
        private const val MAX_DURATION_PER_STEP_MS = 100L
        private const val MIN_STROKE_DURATION_MS = 100L
    }
}