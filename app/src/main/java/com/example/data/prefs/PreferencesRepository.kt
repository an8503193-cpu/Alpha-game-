package com.example.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.quizFriendDataStore: DataStore<Preferences> by preferencesDataStore(name = "quizfriend_prefs")

data class AppPreferences(
    val musicEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val musicVolume: Float = 0.45f,
    val sfxVolume: Float = 0.8f,
    val musicAmbiance: String = "Menu Épique",
    val animationsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val adsEnabled: Boolean = true,
    val adFreeUnlocked: Boolean = false,
    val completedGamesSinceAd: Int = 0,
    val themeId: String = "dark_gaming",
    val shareConsentAccepted: Boolean = false
)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val SFX_ENABLED = booleanPreferencesKey("sfx_enabled")
        val MUSIC_VOLUME = floatPreferencesKey("music_volume")
        val SFX_VOLUME = floatPreferencesKey("sfx_volume")
        val MUSIC_AMBIANCE = stringPreferencesKey("music_ambiance")
        val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val ADS_ENABLED = booleanPreferencesKey("ads_enabled")
        val AD_FREE_UNLOCKED = booleanPreferencesKey("ad_free_unlocked")
        val COMPLETED_GAMES_SINCE_AD = intPreferencesKey("completed_games_since_ad")
        val THEME_ID = stringPreferencesKey("theme_id")
        val SHARE_CONSENT = booleanPreferencesKey("share_consent")
    }

    val preferencesFlow: Flow<AppPreferences> = context.quizFriendDataStore.data.map { prefs ->
        AppPreferences(
            musicEnabled = prefs[Keys.MUSIC_ENABLED] ?: true,
            sfxEnabled = prefs[Keys.SFX_ENABLED] ?: true,
            musicVolume = prefs[Keys.MUSIC_VOLUME] ?: 0.45f,
            sfxVolume = prefs[Keys.SFX_VOLUME] ?: 0.8f,
            musicAmbiance = prefs[Keys.MUSIC_AMBIANCE] ?: "Menu Épique",
            animationsEnabled = prefs[Keys.ANIMATIONS_ENABLED] ?: true,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            adsEnabled = prefs[Keys.ADS_ENABLED] ?: true,
            adFreeUnlocked = prefs[Keys.AD_FREE_UNLOCKED] ?: false,
            completedGamesSinceAd = prefs[Keys.COMPLETED_GAMES_SINCE_AD] ?: 0,
            themeId = prefs[Keys.THEME_ID] ?: "dark_gaming",
            shareConsentAccepted = prefs[Keys.SHARE_CONSENT] ?: false
        )
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.quizFriendDataStore.edit { it[Keys.MUSIC_ENABLED] = enabled }
    }

    suspend fun setSfxEnabled(enabled: Boolean) {
        context.quizFriendDataStore.edit { it[Keys.SFX_ENABLED] = enabled }
    }

    suspend fun setMusicVolume(volume: Float) {
        context.quizFriendDataStore.edit { it[Keys.MUSIC_VOLUME] = volume.coerceIn(0f, 1f) }
    }

    suspend fun setSfxVolume(volume: Float) {
        context.quizFriendDataStore.edit { it[Keys.SFX_VOLUME] = volume.coerceIn(0f, 1f) }
    }

    suspend fun setMusicAmbiance(ambiance: String) {
        context.quizFriendDataStore.edit { it[Keys.MUSIC_AMBIANCE] = ambiance }
    }

    suspend fun setAnimationsEnabled(enabled: Boolean) {
        context.quizFriendDataStore.edit { it[Keys.ANIMATIONS_ENABLED] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.quizFriendDataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setAdsEnabled(enabled: Boolean) {
        context.quizFriendDataStore.edit { it[Keys.ADS_ENABLED] = enabled }
    }

    suspend fun setAdFreeUnlocked(unlocked: Boolean) {
        context.quizFriendDataStore.edit { it[Keys.AD_FREE_UNLOCKED] = unlocked }
    }

    suspend fun incrementCompletedGamesSinceAd(): Int {
        var next = 0
        context.quizFriendDataStore.edit { prefs ->
            next = (prefs[Keys.COMPLETED_GAMES_SINCE_AD] ?: 0) + 1
            prefs[Keys.COMPLETED_GAMES_SINCE_AD] = if (next >= 3) 0 else next
        }
        return next
    }

    suspend fun setThemeId(themeId: String) {
        context.quizFriendDataStore.edit { it[Keys.THEME_ID] = themeId }
    }

    suspend fun setShareConsent(accepted: Boolean) {
        context.quizFriendDataStore.edit { it[Keys.SHARE_CONSENT] = accepted }
    }
}
