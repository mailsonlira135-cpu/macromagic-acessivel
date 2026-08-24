package com.macromagic.accessibility

import android.content.Context
import org.json.JSONObject

/**
 * Small local repository for user-created gesture profiles.
 *
 * Profiles are intentionally stored only on-device. No credentials, screen
 * contents, or event streams are persisted.
 */
class ProfileRepository(context: Context) {
    data class Profile(
        val id: String,
        val name: String,
        val startX: Float,
        val startY: Float,
        val offsetY: Float,
        val steps: Int = MacroExecutionEngine.DEFAULT_STEPS,
        val durationPerStepMs: Long = MacroExecutionEngine.DEFAULT_DURATION_PER_STEP_MS,
    ) {
        fun toDragRequest() = MacroExecutionEngine.DragRequest(
            startX = startX,
            startY = startY,
            offsetY = offsetY,
            steps = steps,
            durationPerStepMs = durationPerStepMs,
        )
    }

    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    fun list(): List<Profile> =
        preferences.all.keys
            .mapNotNull { id -> preferences.getString(id, null)?.let { decode(id, it) } }
            .sortedBy { it.name.lowercase() }

    fun get(id: String): Profile? =
        preferences.getString(id, null)?.let { decode(id, it) }

    fun save(profile: Profile): Boolean {
        require(profile.id.isNotBlank()) { "Profile id cannot be blank." }
        require(profile.name.isNotBlank()) { "Profile name cannot be blank." }
        return preferences.edit().putString(profile.id, encode(profile)).commit()
    }

    fun delete(id: String): Boolean =
        preferences.edit().remove(id).commit()

    private fun encode(profile: Profile): String = JSONObject()
        .put("name", profile.name)
        .put("startX", profile.startX)
        .put("startY", profile.startY)
        .put("offsetY", profile.offsetY)
        .put("steps", profile.steps)
        .put("durationPerStepMs", profile.durationPerStepMs)
        .toString()

    private fun decode(id: String, raw: String): Profile? = runCatching {
        val json = JSONObject(raw)
        Profile(
            id = id,
            name = json.getString("name"),
            startX = json.getDouble("startX").toFloat(),
            startY = json.getDouble("startY").toFloat(),
            offsetY = json.getDouble("offsetY").toFloat(),
            steps = json.optInt("steps", MacroExecutionEngine.DEFAULT_STEPS),
            durationPerStepMs = json.optLong(
                "durationPerStepMs",
                MacroExecutionEngine.DEFAULT_DURATION_PER_STEP_MS,
            ),
        )
    }.getOrNull()

    companion object {
        private const val PREFERENCES_NAME = "macromagic_profiles"
    }
}