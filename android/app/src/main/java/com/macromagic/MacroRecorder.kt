package com.macromagic.shizuku

data class RecordedStep(
    val stepNumber: Int,
    val description: String,
    val commandText: String
)

class MacroRecorder {
    private val recordedSteps = mutableListOf<RecordedStep>()
    private var isRecording = false
    private var lastEventTime = 0Long

    fun startRecording() {
        recordedSteps.clear()
        isRecording = true
        lastEventTime = System.currentTimeMillis()
    }

    fun stopRecording(): List<RecordedStep> {
        isRecording = false
        return recordedSteps.toList()
    }

    fun recordTap(x: Float, y: Float) {
        if (!isRecording) return
        
        // Calcula o tempo de espera desde a última ação executada na tela
        val currentTime = System.currentTimeMillis()
        val delayTime = currentTime - lastEventTime
        
        if (delayTime > 10) {
            val stepIndex = recordedSteps.size + 1
            recordedSteps.add(RecordedStep(
                stepNumber = stepIndex,
                description = "Aguardar $delayTime ms",
                commandText = "wait $delayTime"
            ))
        }

        // Registra o evento de toque capturado nas coordenadas alvo
        val stepIndex = recordedSteps.size + 1
        recordedSteps.add(RecordedStep(
            stepNumber = stepIndex,
            description = "Toque em X: ${x.toInt()}, Y: ${y.toInt()}",
            commandText = "tap ${x.toInt()} ${y.toInt()}"
        ))
        
        lastEventTime = currentTime
    }

    fun recordSwipe(x1: Float, y1: Float, x2: Float, y2: Float, duration: Long) {
        if (!isRecording) return
        
        val currentTime = System.currentTimeMillis()
        val delayTime = currentTime - lastEventTime
        
        if (delayTime > 10) {
            val stepIndex = recordedSteps.size + 1
            recordedSteps.add(RecordedStep(
                stepNumber = stepIndex,
                description = "Aguardar $delayTime ms",
                commandText = "wait $delayTime"
            ))
        }

        val stepIndex = recordedSteps.size + 1
        recordedSteps.add(RecordedStep(
            stepNumber = stepIndex,
            description = "Arrasto de (${x1.toInt()}, ${y1.toInt()}) até (${x2.toInt()}, ${y2.toInt()})",
            commandText = "swipe ${x1.toInt()} ${y1.toInt()} ${x2.toInt()} ${y2.toInt()} $duration"
        ))
        
        lastEventTime = currentTime
    }

    fun updateStepCommand(stepNumber: Int, newCommand: String) {
        val index = recordedSteps.indexOfFirst { it.stepNumber == stepNumber }
        if (index != -1) {
            val currentStep = recordedSteps[index]
            recordedSteps[index] = currentStep.copy(commandText = newCommand)
        }
    }
}

