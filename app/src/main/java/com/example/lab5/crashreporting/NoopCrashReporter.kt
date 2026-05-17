package com.example.lab5.crashreporting

class NoopCrashReporter : CrashReporter {
    override fun log(message: String) = Unit
    override fun setContext(key: String, value: String?) = Unit
    override fun setUserId(userId: String?) = Unit
    override fun recordNonFatal(message: String, error: Throwable) = Unit
    override fun crash(message: String): Nothing = throw RuntimeException(message)
}
