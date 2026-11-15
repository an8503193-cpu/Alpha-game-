package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object SoundEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var musicJob: Job? = null

    var sfxEnabled: Boolean = true
    var sfxVolume: Float = 0.8f
    var musicEnabled: Boolean = false
    var musicVolume: Float = 0.4f

    val ambiances = listOf(
        "Menu Épique",
        "Salon Détente",
        "Ambiance Quiz",
        "Suspense Chrono",
        "Victoire Légendaire",
        "Encouragement",
        "Classement Arena"
    )

    fun playCorrect(volumeScale: Float = sfxVolume) {
        if (!sfxEnabled || volumeScale <= 0.01f) return
        scope.launch {
            playTone(523.25, 90, volumeScale * 0.4f)
            playTone(659.25, 110, volumeScale * 0.45f)
            playTone(783.99, 150, volumeScale * 0.5f)
        }
    }

    fun playWrong(volumeScale: Float = sfxVolume) {
        if (!sfxEnabled || volumeScale <= 0.01f) return
        scope.launch {
            playTone(260.0, 120, volumeScale * 0.35f)
            playTone(196.0, 180, volumeScale * 0.35f)
        }
    }

    fun playTick(volumeScale: Float = sfxVolume) {
        if (!sfxEnabled || volumeScale <= 0.01f) return
        scope.launch {
            playTone(880.0, 35, volumeScale * 0.25f)
        }
    }

    fun playVictory(volumeScale: Float = sfxVolume) {
        if (!sfxEnabled || volumeScale <= 0.01f) return
        scope.launch {
            listOf(523.25, 659.25, 783.99, 1046.5).forEach { freq ->
                playTone(freq, 130, volumeScale * 0.45f)
            }
        }
    }

    fun playReaction(volumeScale: Float = sfxVolume) {
        if (!sfxEnabled || volumeScale <= 0.01f) return
        scope.launch {
            playTone(740.0, 55, volumeScale * 0.3f)
        }
    }

    fun updateMusicState(enabled: Boolean, volume: Float, ambiance: String) {
        musicEnabled = enabled
        musicVolume = volume
        musicJob?.cancel()
        if (!enabled || volume <= 0.01f) return
        val notes = when (ambiance) {
            "Suspense Chrono" -> listOf(220.0, 233.08, 220.0, 246.94)
            "Victoire Légendaire" -> listOf(392.0, 493.88, 587.33, 783.99)
            "Salon Détente" -> listOf(261.63, 329.63, 392.0, 329.63)
            "Ambiance Quiz" -> listOf(293.66, 349.23, 440.0, 349.23)
            else -> listOf(261.63, 311.13, 392.0, 523.25)
        }
        musicJob = scope.launch {
            var idx = 0
            while (isActive && musicEnabled) {
                playTone(notes[idx % notes.size], 280, musicVolume * 0.14f)
                idx++
                delay(650)
            }
        }
    }

    private fun playTone(freqHz: Double, durationMs: Int, gain: Float) {
        val sampleRate = 22050
        val count = (sampleRate * durationMs / 1000).coerceAtLeast(1)
        val samples = ShortArray(count)
        for (i in 0 until count) {
            val env = if (i < count / 8) i / (count / 8.0) else (count - i) / (count * 7.0 / 8.0)
            val value = sin(2.0 * PI * i * freqHz / sampleRate) * env * gain.coerceIn(0f, 1f)
            samples[i] = (value * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(samples, 0, samples.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 15L)
            track.stop()
            track.release()
        } catch (_: Throwable) {
            // Safe no-op in headless/Robolectric environments
        }
    }
}
