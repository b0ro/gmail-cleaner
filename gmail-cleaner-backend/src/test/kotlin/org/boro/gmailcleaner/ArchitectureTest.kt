package org.boro.gmailcleaner

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

const val BASE_PACKAGE: String = "org.boro.gmailcleaner"

private const val DOMAIN = "Domain"
private const val ADAPTERS = "Adapters"
private const val INFRASTRUCTURE = "Infrastructure"

private const val DOMAIN_PACKAGE = "$BASE_PACKAGE.domain.."
private const val ADAPTER_PACKAGE = "$BASE_PACKAGE.adapter.."
private const val INFRASTRUCTURE_PACKAGE = "$BASE_PACKAGE.infrastructure.."
private const val REST_ADAPTER_PACKAGE = "$BASE_PACKAGE.adapter.rest.."
private const val CONTROLLER_SUFFIX = "Controller"

class ArchitectureTest {
    private val classes: JavaClasses =
        ClassFileImporter()
            .withImportOption(DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE)

    @Test
    fun `classes should not throw generic exceptions`() = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS.check(classes)

    @Test
    fun `classes should be free of cycles`() =
        slices().matching("$BASE_PACKAGE.(*)..")
            .should().beFreeOfCycles()
            .check(classes)

    @Test
    fun `domain should be framework free`() =
        classes()
            .that().resideInAPackage(DOMAIN_PACKAGE)
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(
                "$BASE_PACKAGE..",
                "java..",
                "kotlin..",
                "jakarta.persistence..",
                "jakarta.transaction..",
                "jakarta.validation..",
                "org.jetbrains..",
                "org.hibernate..",
            )
            .check(classes)

    @Test
    fun `adapters should not depend on infrastructure`() =
        layeredArchitecture()
            .consideringAllDependencies()
            .layer(DOMAIN).definedBy(DOMAIN_PACKAGE)
            .layer(ADAPTERS).definedBy(ADAPTER_PACKAGE)
            .layer(INFRASTRUCTURE).definedBy(INFRASTRUCTURE_PACKAGE)
            .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(INFRASTRUCTURE)
            .whereLayer(INFRASTRUCTURE).mayNotBeAccessedByAnyLayer()
            .check(classes)

    @Test
    fun `adapters should not depend on each other`() {
        val prefix = ADAPTER_PACKAGE.dropLast(2)
        val adapterPackages =
            classes
                .map { it.packageName }
                .filter { it.startsWith(prefix) }
                .distinct()

        adapterPackages.forEach { adapter ->
            noClasses()
                .that()
                .resideInAPackage("$adapter..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(*adapterPackages.filterNot { it == adapter }.map { "$it.." }.toTypedArray())
                .check(classes)
        }
    }

    @Test
    fun `spring beans should be created only in configuration classes`() =
        classes()
            .should().notBeAnnotatedWith(Component::class.java)
            .andShould().notBeAnnotatedWith(Service::class.java)
            .andShould().notBeAnnotatedWith(Repository::class.java)
            .andShould().notBeAnnotatedWith(Controller::class.java)
            .check(classes)

    @Test
    fun `controller should have proper suffix`() =
        classes()
            .that().areAnnotatedWith(RestController::class.java).and().resideInAPackage(REST_ADAPTER_PACKAGE)
            .should().haveSimpleNameEndingWith(CONTROLLER_SUFFIX)
            .check(classes)
}
