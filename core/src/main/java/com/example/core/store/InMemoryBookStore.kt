package com.example.core.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryBookStore(
    initialBooks: List<BookRecord> = defaultBooks
) : BookStore {
    private val books = MutableStateFlow(initialBooks)

    override fun observeBooks(): Flow<List<BookRecord>> = books

    override fun observeBook(bookId: String): Flow<BookRecord?> = books.map { items ->
        items.firstOrNull { it.id == bookId }
    }

    override suspend fun toggleFavorite(bookId: String) {
        books.update { items ->
            items.map { record ->
                if (record.id == bookId) record.copy(isFavorite = !record.isFavorite) else record
            }
        }
    }

    companion object {
        val defaultBooks = listOf(
            BookRecord("clean-architecture", "Clean Architecture", "Robert C. Martin", "Практическое руководство по устойчивой архитектуре приложений и разделению ответственности.", "Architecture", 2017, "Reading", 4.8, true),
            BookRecord("effective-kotlin", "Effective Kotlin", "Marcin Moskala", "Книга про идиоматичный Kotlin, читаемость, безопасность и лучшие практики разработки.", "Kotlin", 2024, "Planned", 4.7, false),
            BookRecord("android-development-patterns", "Android Development Patterns", "Alex Forrester", "Подборка подходов к построению Android-приложений, модульности и поддерживаемости.", "Android", 2023, "Reading", 4.4, true),
            BookRecord("domain-driven-design", "Domain-Driven Design Distilled", "Vaughn Vernon", "Краткое и прикладное введение в DDD с акцентом на модели и границы контекстов.", "Design", 2016, "Finished", 4.6, false),
            BookRecord("refactoring", "Refactoring", "Martin Fowler", "Классика про безопасное улучшение кода, работу с техдолгом и небольшие понятные изменения.", "Engineering", 2018, "Finished", 4.9, true),
            BookRecord("pragmatic-programmer", "The Pragmatic Programmer", "David Thomas", "Советы по профессиональной разработке, принятию решений и долгосрочному росту инженера.", "Engineering", 2019, "Planned", 4.8, false),
            BookRecord("jetpack-compose-essentials", "Jetpack Compose Essentials", "Rajesh Kumar", "Практический справочник по Compose: состояния, layout, material-компоненты и производительность.", "Compose", 2024, "Reading", 4.3, false),
            BookRecord("designing-data-intensive-applications", "Designing Data-Intensive Applications", "Martin Kleppmann", "Глубокий разбор хранения, потоков данных, согласованности и масштабирования систем.", "Systems", 2017, "Planned", 5.0, false)
        )
    }
}
