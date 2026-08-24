package com.macromagic.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/**
 * Entry point declared in AndroidManifest.xml. Gestures are dispatched only
 * through MacroExecutionEngine after the user enables this service.
 */
class MacroAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit
}