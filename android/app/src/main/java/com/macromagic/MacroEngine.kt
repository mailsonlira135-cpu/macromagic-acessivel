package com.macromagic.shizuku

import kotlinx.coroutines.delay

sealed class ParseResult {
    data class Success(val commands: List<MacroCommand>) : ParseResult()
    data class Error(val lineNumber: Int, val message: String) : ParseResult()
}

sealed class MacroCommand {
    data class Tap(val x: Float, val y: Float) : MacroCommand()
    data class Hold(val x: Float, val y: Float, val duration: Long) : MacroCommand()
    data class Swipe(val x1: Float, val y1: Float, val x2: Float, val y2: Float, val duration: Long) : MacroCommand()
    data class Wait(val duration: Long) : MacroCommand()
}

class MacroEngine {
    private var isRunning = false

    fun parseScript(script: String): ParseResult {
        val lines = script.lines()
        val commands = mutableListOf<MacroCommand>()

        for ((index, rawLine) in lines.withIndex()) {
            val line = rawLine.trim()
            if (line.isEmpty() || line.startsWith("#")) continue

            val parts = line.split("\\s+".toRegex())
            val commandName = parts[0].lowercase()
            val lineNumber = index + 1

            try {
                when (commandName) {
                    "tap" -> {
                        if (parts.size < 3) return ParseResult.Error(lineNumber, "Comando inválido: faltam coordenadas X e Y")
                        val x = parts[1].toFloatOrNull() ?: return ParseResult.Error(lineNumber, "Coordenada X inválida")
                        val y = parts[2].toFloatOrNull() ?: return ParseResult.Error(lineNumber, "Coordenada Y inválida")
                        commands.add(MacroCommand.Tap(x, y))
                    }
                    "hold" -> {
                        if (parts.size < 4) return ParseResult.Error(lineNumber, "Comando inválido: parâmetros insuficientes")
                        val x = parts[1].toFloatOrNull() ?: return ParseResult.Error(lineNumber, "Coordenada X inválida")
                        val y = parts[2].toFloatOrNull() ?: return ParseResult.Error(lineNumber, "Coordenada Y inválida")
                        val duration = parts[3].toLongOrNull() ?: return ParseResult.Error(lineNumber, "Duração inválida")
                        commands.add(MacroCommand.Hold(x, y, duration))
                    }
                    "swipe" -> {
                        if (parts.size < 6) return ParseResult.Error(lineNumber, "Comando inválido: faltam parâmetros do gesto")
                        val x1 = parts[1].toFloatOrNull() ?: return ParseResult.Error(lineNumber, "Coordenada X1 inválida")
                        val y1 = parts[2].toFloatOrNull() ?: return ParseResult.Error(lineNumber, "Coordenada Y1 inválida")
                        val x2 = parts[3].toFloatOrNull() ?: return ParseResult.Error(lineNumber, "Coordenada X2 inválida")
                        val y2 = parts[4].toFloatOrNull() ?: return ParseResult.Error(lineNumber, "Coordenada Y2 inválida")
                        val duration = parts[5].toLongOrNull() ?: return ParseResult.Error(lineNumber, "Duração inválida")
                        commands.add(MacroCommand.Swipe(x1, y1, x2, y2, duration))
                    }
                    "wait" -> {
                        if (parts.size < 2) return ParseResult.Error(lineNumber, "Comando inválido: informe o tempo de espera")
                        val duration = parts[1].toLongOrNull() ?: return ParseResult.Error(lineNumber, "Duração inválida")
                        commands.add(MacroCommand.Wait(duration))
                    }
                    "stop" -> break
                    else -> return ParseResult.Error(lineNumber, "Comando desconhecido ou inválido")
                }
            } catch (e: Exception) {
                return ParseResult.Error(lineNumber, "Erro crítico ao processar parâmetros")
            }
        }
        return ParseResult.Success(commands)
    }

    suspend fun executeScript(commands: List<MacroCommand>, onGestureDispatch: (MacroCommand) -> Unit) {
        isRunning = true
        for (command in commands) {
            if (!isRunning) break
            when (command) {
                is MacroCommand.Wait -> delay(command.duration)
                else -> onGestureDispatch(command)
            }
        }
        isRunning = false
    }

    fun stopExecution() {
        isRunning = false
    }
}

