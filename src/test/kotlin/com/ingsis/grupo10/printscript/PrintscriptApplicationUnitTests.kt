package com.ingsis.grupo10.printscript

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertTrue

@SpringBootTest
@TestPropertySource(properties = ["spring.main.web-application-type=none"])
class PrintscriptApplicationUnitTests {

    @Test
    fun `application class is annotated with SpringBootApplication`() {
        val annotations = PrintscriptApplication::class.java.annotations
        val hasSpringBootApplication = annotations.any {
            it.annotationClass.simpleName == "SpringBootApplication"
        }
        assertTrue(hasSpringBootApplication)
    }
}