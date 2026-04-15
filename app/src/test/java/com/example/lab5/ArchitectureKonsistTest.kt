package com.example.lab5

import com.lemonappdev.konsist.api.Konsist
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchitectureKonsistTest {
    @Test
    fun domain_modules_do_not_depend_on_android_framework() {
        val domainFiles = Konsist.scopeFromProject().files.filter { it.path.contains("feature") && it.path.contains("\\domain\\") }
        assertTrue(domainFiles.none { file -> file.imports.any { it.name.startsWith("android.") || it.name.startsWith("androidx.compose") } })
    }

    @Test
    fun data_modules_do_not_depend_on_ui_components() {
        val dataFiles = Konsist.scopeFromProject().files.filter { it.path.contains("feature") && it.path.contains("\\data\\") }
        assertTrue(dataFiles.none { file -> file.imports.any { it.name.contains(".ui.") || it.name.contains("androidx.compose") } })
    }

    @Test
    fun feature_modules_do_not_depend_on_each_other_directly() {
        val files = Konsist.scopeFromProject().files.filter { it.path.contains("feature") }
        val invalidCatalogImports = files.filter { it.path.contains("feature\\catalog") }.flatMap { it.imports }
            .any { it.name.contains("feature.favorites.domain") || it.name.contains("feature.favorites.data") || it.name.contains("feature.favorites.ui") }
        val invalidFavoritesImports = files.filter { it.path.contains("feature\\favorites") }.flatMap { it.imports }
            .any { it.name.contains("feature.catalog.domain") || it.name.contains("feature.catalog.data") || it.name.contains("feature.catalog.ui") }
        assertTrue(!invalidCatalogImports && !invalidFavoritesImports)
    }

    @Test
    fun use_cases_are_located_in_domain_modules() {
        val useCaseFiles = Konsist.scopeFromProject().files.filter { it.hasNameContaining("UseCase") }
        assertTrue(useCaseFiles.all { it.path.contains("\\domain\\") })
    }

    @Test
    fun repositories_are_interfaces_in_domain_and_implementations_in_data() {
        val scope = Konsist.scopeFromProject()
        val repositoryInterfaces = scope.interfaces().filter { it.name.endsWith("Repository") }
        val repositoryImplementations = scope.classes().filter { it.name.endsWith("RepositoryImpl") }
        assertTrue(repositoryInterfaces.all { it.containingFile?.path?.contains("\\domain\\") == true })
        assertTrue(repositoryImplementations.all { it.containingFile?.path?.contains("\\data\\") == true })
    }
}
