package com.macromagic.shizuku

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MacroTrainingSimulatorScreen() {
    var isSimulating by remember { mutableStateOf(false) }
    var currentCycle by remember { mutableStateOf(0) }
    var trajectoryPoints = remember { mutableStateListOf<Offset>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Modo Treinamento / Simulador", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Ambiente virtual seguro para testar trajetórias e velocidade.", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // Painel de Telemetria exigido no escopo técnico
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Ciclos: $currentCycle", fontSize = 14.sp)
            Text("Status: ${if (isSimulating) "▶ Executando" else "⏹ Parado"}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tela Virtual de Simulação Gráfica
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black.copy(alpha = 0.05f))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Renderiza na tela a trajetória simulada da macro de acessibilidade
                if (trajectoryPoints.size > 1) {
                    for (i in 0 until trajectoryPoints.size - 1) {
                        drawLine(
                            color = Color(0xFF2196F3),
                            start = trajectoryPoints[i],
                            end = trajectoryPoints[i + 1],
                            strokeWidth = 5f
                        )
                    }
                }
                // Desenha o cursor virtual ativo
                trajectoryPoints.lastOrNull()?.let { lastPoint ->
                    drawCircle(color = Color.Green, radius = 20f, center = lastPoint)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Barra de Controle Integrada (Executar, Pausar, Parar, Próximo Passo)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    isSimulating = true
                    currentCycle++
                    // Simula coordenadas fictícias de um movimento vertical para teste visual
                    trajectoryPoints.clear()
                    trajectoryPoints.add(Offset(500f, 1000f))
                    trajectoryPoints.add(Offset(500f, 1200f))
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("▶ Executar", fontSize = 11.sp)
            }

            Button(
                onClick = { isSimulating = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("⏸ Pausar", fontSize = 11.sp)
            }

            Button(
                onClick = {
                    isSimulating = false
                    currentCycle = 0
                    trajectoryPoints.clear()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("⏹ Parar", fontSize = 11.sp)
            }
        }
    }
}

