package com.macromagic.shizuku

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CompensationUiScreen() {
    var sliderValue by remember { mutableStateOf(50f) }
    var selectedPreset by remember { mutableStateOf("Médio") }
    
    // Variáveis para os campos manuais/personalizados exigidos na especificação
    var intensityX by remember { mutableStateOf("0") }
    var intensityY by remember { mutableStateOf("35") }
    var speed by remember { mutableStateOf("50") }
    var interval by remember { mutableStateOf("40") }
    var steps by remember { mutableStateOf("5") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Compensação Vertical",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Configura pequenos movimentos verticais durante uma macro autorizada de acessibilidade.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Controle deslizante estruturado conforme o escopo (0% a 100%)
        Text(text = "Intensidade Geral: ${sliderValue.toInt()}%", fontWeight = FontWeight.SemiBold)
        Slider(
            value = sliderValue,
            onValueChange = { 
                sliderValue = it
                selectedPreset = "Personalizado"
            },
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Linha de Presets: Leve, Médio, Forte, Personalizado
        Text(text = "Presets", fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val presets = listOf("Leve", "Médio", "Forte", "Personalizado")
            presets.forEach { preset ->
                FilterChip(
                    selected = (selectedPreset == preset),
                    onClick = {
                        selectedPreset = preset
                        when (preset) {
                            "Leve" -> { sliderValue = 20f; intensityY = "15"; speed = "30"; interval = "50"; steps = "3" }
                            "Médio" -> { sliderValue = 50f; intensityY = "35"; speed = "50"; interval = "40"; steps = "5" }
                            "Forte" -> { sliderValue = 80f; intensityY = "60"; speed = "70"; interval = "30"; steps = "8" }
                        }
                    },
                    label = { Text(preset) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        // Ajustes manuais detalhados exigidos no escopo técnico
        Text(text = "Ajustes Avançados Manuais", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = intensityX, onValueChange = { intensityX = it }, label = { Text("Intensidade X") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = intensityY, onValueChange = { intensityY = it }, label = { Text("Intensidade Y") }, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = speed, onValueChange = { speed = it }, label = { Text("Velocidade") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = interval, onValueChange = { interval = it }, label = { Text("Intervalo (ms)") }, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = steps, onValueChange = { steps = it }, label = { Text("Número de passos") }, modifier = Modifier.fillMaxWidth())
    }
}

