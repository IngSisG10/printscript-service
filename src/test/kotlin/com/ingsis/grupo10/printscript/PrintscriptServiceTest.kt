package com.ingsis.grupo10.printscript

import com.ingsis.grupo10.printscript.printscript.service.PrintscriptService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.io.ByteArrayInputStream

@SpringBootTest
class PrintscriptServiceTest {

    private val printscriptService = PrintscriptService()

    // Helper function to avoid null issues with Mockito in Kotlin
    private fun <T> any(): T = ArgumentMatchers.any()

    @Test
    fun execute_valid_code_returns_output() {
        val code = "println(\"Hello World\");"
        val codeStream = ByteArrayInputStream(code.toByteArray())
        val version = "1.0"

        val response = printscriptService.execute(codeStream, version)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(MediaType.TEXT_PLAIN, response.headers.contentType)
        assertNotNull(response.body)
        assertTrue(response.body is InputStreamResource)
    }

    @Test
    fun execute_with_variables_returns_correct_output() {
        val code = "let x: number = 42; println(x);"
        val codeStream = ByteArrayInputStream(code.toByteArray())
        val version = "1.1"

        val response = printscriptService.execute(codeStream, version)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun execute_invalid_version_throws_exception() {
        val code = "println(\"Hello World\");"
        val codeStream = ByteArrayInputStream(code.toByteArray())
        val version = "invalid"

        val response = printscriptService.execute(codeStream, version)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun execute_syntax_error_throws_exception() {
        val code = "println(\"unclosed string"
        val codeStream = ByteArrayInputStream(code.toByteArray())
        val version = "1.0"
        val response = printscriptService.execute(codeStream, version)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun format_with_spacing_rules_returns_formatted_code() {
        val code = "let x=5;println(x);"
        val codeStream = ByteArrayInputStream(code.toByteArray())
        val config = """
            {
                "enforce-spacing-around-equals": true,
                "indent": 2
            }
        """.trimIndent()
        val version = "1.0"

        val response = printscriptService.format(codeStream, config, version)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertTrue(response.body is String)
    }

    @Test
    fun format_without_spacing_rules_returns_formatted_code() {
        val code = "let x = 5; println(x);"
        val codeStream = ByteArrayInputStream(code.toByteArray())
        val config = """
            {
                "enforce-spacing-around-equals": false,
                "indent": 4
            }
        """.trimIndent()
        val version = "1.0"

        val response = printscriptService.format(codeStream, config, version)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun format_invalid_config_throws_exception() {
        val code = "let x = 5;"
        val codeStream = ByteArrayInputStream(code.toByteArray())
        val config = "invalid json"
        val version = "1.0"

        assertThrows<IllegalArgumentException> {
            printscriptService.format(codeStream, config, version)
        }
    }

    @Test
    fun verify_camel_case_valid_code_returns_success() {
        val code = "let variableName: number = 5;"
        val codeStream = ByteArrayInputStream(code.toByteArray())
        val config = """
            {
                "identifier_format": "camel case"
            }
        """.trimIndent()
        val version = "1.0"

        val response = printscriptService.verify(codeStream, config, version)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun verify_camel_case_invalid_code_returns_error() {
        val code = "let snake_case_variable: number = 5;"
        val codeStream = ByteArrayInputStream(code.toByteArray())
        val config = """
            {
                "identifier_format": "camel case"
            }
        """.trimIndent()
        val version = "1.0"

        val response = printscriptService.verify(codeStream, config, version)

        assertNotNull(response.body)
        assertTrue(response.body.toString().contains("LintResultDTO(errors=[LintErrorDTO(message=Invalid camelCase identifier at row 0 and position 4, type=InvalidCamelCaseException, segment=1)])"))
    }

    @Test
    fun verify_snake_case_valid_code_returns_success() {
        val code = "let snake_case_variable: number = 5;"
        val codeStream = ByteArrayInputStream(code.toByteArray())
        val config = """
            {
                "identifier_format": "snake case"
            }
        """.trimIndent()
        val version = "1.0"

        val response = printscriptService.verify(codeStream, config, version)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun verify_empty_config_uses_defaults() {
        val code = "let x: number = 5;"
        val codeStream = ByteArrayInputStream(code.toByteArray())
        val config = "{}"
        val version = "1.0"

        val response = printscriptService.verify(codeStream, config, version)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }
}