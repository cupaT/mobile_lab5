package com.example.lab5.crashreporting

class CompositeCrashReporter(
    private val reporters: List<CrashReporter>
) : CrashReporter {
    override fun log(message: String) {
        reporters.forEach { it.log(message) }
    }

    override fun setContext(key: String, value: String?) {
        reporters.forEach { it.setContext(key, value) }
    }

    override fun setUserId(userId: String?) {
        reporters.forEach { it.setUserId(userId) }
    }

    override fun recordNonFatal(message: String, error: Throwable) {
        reporters.forEach { it.recordNonFatal(message, error) }
    }

    override fun crash(message: String): Nothing {
        log(message)
        throw RuntimeException(message)
    }
}
