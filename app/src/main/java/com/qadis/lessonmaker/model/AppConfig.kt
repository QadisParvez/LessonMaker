package com.qadis.lessonmaker.model

data class AppConfig(
    val apiBaseUrl: String = "https://your-domain.com/fypAPI/api/",
    val enableVoiceNotes: Boolean = true,
    val enableBookmarks: Boolean = true,
    val enableOfflineMode: Boolean = true,
    val maxVoiceNoteDuration: Long = 300000, // 5 minutes in milliseconds
    val autoSaveInterval: Long = 30000, // 30 seconds
    val enableRecoveryMode: Boolean = true
)

object ConfigManager {
    private var config = AppConfig()
    
    fun getConfig(): AppConfig = config
    
    fun updateConfig(newConfig: AppConfig) {
        config = newConfig
    }
    
    fun getApiBaseUrl(): String = config.apiBaseUrl
    fun isVoiceNotesEnabled(): Boolean = config.enableVoiceNotes
    fun isBookmarksEnabled(): Boolean = config.enableBookmarks
    fun isOfflineModeEnabled(): Boolean = config.enableOfflineMode
    fun getMaxVoiceNoteDuration(): Long = config.maxVoiceNoteDuration
    fun getAutoSaveInterval(): Long = config.autoSaveInterval
    fun isRecoveryModeEnabled(): Boolean = config.enableRecoveryMode
}