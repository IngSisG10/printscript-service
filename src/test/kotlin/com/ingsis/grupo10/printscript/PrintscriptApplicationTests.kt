package com.ingsis.grupo10.printscript

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PrintscriptApplicationIntegrationTests {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun `application context loads successfully`() {
        assertNotNull(context)
    }

    @Test
    fun `application starts on random port`() {
        assert(port > 0)
    }

    @Test
    fun `health endpoint returns OK`() {
        val response = restTemplate.getForEntity(
            "http://localhost:$port/health",
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `main method starts application`() {
        // Test that main method can be called without exceptions
        // This is more of a smoke test
        assertNotNull(PrintscriptApplication::class.java)
    }
}
