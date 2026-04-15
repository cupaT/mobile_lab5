package com.example.lab5.feature.catalog.data

import com.example.core.books.Book
import com.example.core.books.ReadingStatus

object BookSeed {
    val books = listOf(
        Book("clean-architecture", "Clean Architecture", "Robert C. Martin", "Практическое руководство по устойчивой архитектуре приложений и разделению ответственности.", "Architecture", 2017, ReadingStatus.Reading, 4.8, true),
        Book("effective-kotlin", "Effective Kotlin", "Marcin Moskala", "Книга про идиоматичный Kotlin, читаемость, безопасность и лучшие практики разработки.", "Kotlin", 2024, ReadingStatus.Planned, 4.7, false),
        Book("android-development-patterns", "Android Development Patterns", "Alex Forrester", "Подборка подходов к построению Android-приложений, модульности и поддерживаемости.", "Android", 2023, ReadingStatus.Reading, 4.4, true),
        Book("domain-driven-design", "Domain-Driven Design Distilled", "Vaughn Vernon", "Краткое и прикладное введение в DDD с акцентом на модели и границы контекстов.", "Design", 2016, ReadingStatus.Finished, 4.6, false),
        Book("refactoring", "Refactoring", "Martin Fowler", "Классика про безопасное улучшение кода, работу с техдолгом и небольшие понятные изменения.", "Engineering", 2018, ReadingStatus.Finished, 4.9, true),
        Book("pragmatic-programmer", "The Pragmatic Programmer", "David Thomas", "Советы по профессиональной разработке, принятию решений и долгосрочному росту инженера.", "Engineering", 2019, ReadingStatus.Planned, 4.8, false),
        Book("jetpack-compose-essentials", "Jetpack Compose Essentials", "Rajesh Kumar", "Практический справочник по Compose: состояния, layout, material-компоненты и производительность.", "Compose", 2024, ReadingStatus.Reading, 4.3, false),
        Book("designing-data-intensive-applications", "Designing Data-Intensive Applications", "Martin Kleppmann", "Глубокий разбор хранения, потоков данных, согласованности и масштабирования систем.", "Systems", 2017, ReadingStatus.Planned, 5.0, false)
    )
}
