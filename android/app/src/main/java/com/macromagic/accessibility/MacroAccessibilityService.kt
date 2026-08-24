package com.macromagic.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import com.macromagic.shizuku.MacroCommand

class MacroAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Método obrigatório da API nativa
    }

    override fun onInterrupt() {
        // Chamado quando o sistema interrompe o serviço de acessibilidade
    }

    /**
     * Interpreta os comandos validados pelo MacroEngine e executa os gestos reais na tela
     */
    fun dispatchMacroCommand(command: MacroCommand) {
        val gestureBuilder = GestureDescription.Builder()

        when (command) {
            is MacroCommand.Tap -> {
                val path = Path().apply { moveTo(command.x, command.y) }
                val stroke = GestureDescription.StrokeDescription(path, 0, 50)
                gestureBuilder.addStroke(stroke)
            }
            is MacroCommand.Hold -> {
                val path = Path().apply { moveTo(command.x, command.y) }
                val stroke = GestureDescription.StrokeDescription(path, 0, command.duration)
                gestureBuilder.addStroke(stroke)
            }
            is MacroCommand.Swipe -> {
                val path = Path().apply {
                    moveTo(command.x1, command.y1)
                    lineTo(command.x2, command.y2)
                }
                val stroke = GestureDescription.StrokeDescription(path, 0, command.duration)
                gestureBuilder.addStroke(stroke)
            }
            else -> return
        }

        dispatchGesture(gestureBuilder.build(), object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
            }
            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
            }
        }, null)
    }
}
