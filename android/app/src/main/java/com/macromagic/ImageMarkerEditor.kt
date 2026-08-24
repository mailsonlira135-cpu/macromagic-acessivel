package com.macromagic.shizuku

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MacroMarker(
    val id: String,
    var name: String,
    val x: Float,
    val y: Float,
    val actionType: String
)

@Composable
fun ImageMarkerEditor(
    backgroundImageWidth: Float,
    backgroundImageHeight: Float,
    onMarkerAdded: (MacroMarker) -> Unit
) {
    var markers = remember { mutableStateListOf<MacroMarker>() }
    var selectedPoint by remember { mutableStateOf<Offset?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = selectedPoint?.let { "Coordenada Selecionada - X: ${it.x.toInt()}, Y: ${it.y.toInt()}" } 
                ?: "Toque na imagem para extrair as coordenadas X/Y",
            modifier = Modifier.padding(16.dp),
            fontSize = 14.sp
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        selectedPoint = offset
                        val newMarker = MacroMarker(
                            id = System.currentTimeMillis().toString(),
                            name = "Marcador ${markers.size + 1}",
                            x = offset.x,
                            y = offset.y,
                            actionType = "Tap"
                        )
                        markers.add(newMarker)
                        onMarkerAdded(newMarker)
                    }
                }
        ) {
            markers.forEach { marker ->
                drawCircle(
                    color = Color.Red,
                    radius = 15f,
                    center = Offset(marker.x, marker.y)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = Offset(marker.x, marker.y)
                )
            }
        }
    }
}

