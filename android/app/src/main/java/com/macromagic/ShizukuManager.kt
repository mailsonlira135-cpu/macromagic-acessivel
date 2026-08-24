package com.macromagic.shizuku

import android.content.Context
import android.content.pm.PackageManager
import dev.rikka.shizuku.Shizuku
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ShizukuState {
    NOT_FOUND,      // ❌ Shizuku não encontrado
    UNAUTHORIZED,   // ⚠️ Shizuku encontrado, mas sem permissão
    CONNECTED       // ✅ Shizuku conectado
}

class ShizukuManager(private val context: Context) {

    private val _state = MutableStateFlow(ShizukuState.NOT_FOUND)
    val state: StateFlow<ShizukuState> = _state

    private val bindListener = Shizuku.OnBinderReceivedListener {
        checkStatus()
    }

    private val deadListener = Shizuku.OnBinderDeadListener {
        _state.value = ShizukuState.NOT_FOUND
    }

    fun initialize() {
        Shizuku.addBinderReceivedListener(bindListener)
        Shizuku.addBinderDeadListener(deadListener)
        checkStatus()
    }

    fun destroy() {
        Shizuku.removeBinderReceivedListener(bindListener)
        Shizuku.removeBinderDeadListener(deadListener)
    }

    fun checkStatus() {
        if (!Shizuku.pingBinder()) {
            _state.value = ShizukuState.NOT_FOUND
            return
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            _state.value = ShizukuState.CONNECTED
        } else {
            _state.value = ShizukuState.UNAUTHORIZED
        }
    }

    fun requestPermission(requestCode: Int) {
        if (Shizuku.pingBinder() && Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(requestCode)
        }
    }
}

