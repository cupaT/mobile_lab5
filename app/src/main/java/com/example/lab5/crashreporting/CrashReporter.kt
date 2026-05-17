package com.example.lab5.crashreporting

interface CrashReporter {
    fun log(message: String)
    fun setContext(key: String, value: String?)
    fun setUserId(userId: String?)
    fun recordNonFatal(message: String, error: Throwable)
    fun crash(message: String): Nothing
}
