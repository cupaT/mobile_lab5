package com.example.lab5.crashreporting

import io.appmetrica.analytics.AppMetrica

class AppMetricaCrashReporter : CrashReporter {
    override fun log(message: String) {
        AppMetrica.reportEvent("crash_log", mapOf("message" to message))
    }

    override fun setContext(key: String, value: String?) {
        AppMetrica.reportEvent(
            "crash_context",
            mapOf("key" to key, "value" to value.orEmpty())
        )
    }

    override fun setUserId(userId: String?) {
        AppMetrica.setUserProfileID(userId)
    }

    override fun recordNonFatal(message: String, error: Throwable) {
        AppMetrica.reportError(message, error)
    }

    override fun crash(message: String): Nothing {
        throw RuntimeException(message)
    }
}
