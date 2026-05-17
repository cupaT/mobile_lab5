package com.example.lab5.crashreporting

import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsCrashReporter(
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
) : CrashReporter {
    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun setContext(key: String, value: String?) {
        crashlytics.setCustomKey(key, value.orEmpty())
    }

    override fun setUserId(userId: String?) {
        crashlytics.setUserId(userId.orEmpty())
    }

    override fun recordNonFatal(message: String, error: Throwable) {
        crashlytics.log(message)
        crashlytics.recordException(error)
    }

    override fun crash(message: String): Nothing {
        throw RuntimeException(message)
    }
}
