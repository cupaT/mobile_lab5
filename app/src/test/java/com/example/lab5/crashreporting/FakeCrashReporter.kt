package com.example.lab5.crashreporting

class FakeCrashReporter : CrashReporter {
    data class NonFatal(val message: String, val error: Throwable)

    val logs = mutableListOf<String>()
    val context = mutableMapOf<String, String?>()
    val nonFatals = mutableListOf<NonFatal>()
    var recordedUserId: String? = null

    override fun log(message: String) {
        logs += message
    }

    override fun setContext(key: String, value: String?) {
        context[key] = value
    }

    override fun setUserId(userId: String?) {
        recordedUserId = userId
    }

    override fun recordNonFatal(message: String, error: Throwable) {
        nonFatals += NonFatal(message, error)
    }

    override fun crash(message: String): Nothing {
        throw RuntimeException(message)
    }
}
