package com.example.lab5.crashreporting

class ManualCrashTrigger(
    private val crashReporter: CrashReporter
) {
    fun trigger(context: Map<String, String?> = emptyMap()): Nothing {
        crashReporter.log("Manual crash button clicked")
        context.forEach { (key, value) -> crashReporter.setContext(key, value) }
        crashReporter.setUserId(context["user_email"] ?: context["user_name"])
        crashReporter.crash("Manual crash from lab8 control task")
    }
}
