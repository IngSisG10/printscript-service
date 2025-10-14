package com.ingsis.grupo10.printscript

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertNotNull

@SpringBootTest
class PrintscriptApplicationConfigurationTests {

    @Test
    fun `application loads with test configuration`() {
        assertNotNull(this)
    }

    @Test
    fun contextLoads() {
    }
}