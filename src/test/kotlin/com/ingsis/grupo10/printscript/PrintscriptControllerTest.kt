package com.ingsis.grupo10.printscript

import com.ingsis.grupo10.printscript.printscript.controller.PrintscriptController
import com.ingsis.grupo10.printscript.printscript.service.PrintscriptService
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.ByteArrayInputStream
import java.io.InputStream

@WebMvcTest(PrintscriptController::class)
@Import(PrintscriptControllerTest.MockBeans::class)
class PrintscriptControllerTest {

    @TestConfiguration
    class MockBeans {
        @Bean
        fun printscriptService(): PrintscriptService = mock()
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var printscriptService: PrintscriptService

    // Helper function to avoid null issues with Mockito in Kotlin
    private fun <T> any(): T = ArgumentMatchers.any()

    @Test
    fun execute_returns_stream() {
        val snippet = MockMultipartFile(
            "snippet",
            "code.txt",
            "text/plain",
            "println(\"Hi\")".toByteArray()
        )

        `when`(printscriptService.execute(any<InputStream>(), any<String>())).thenReturn(
            ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(InputStreamResource(ByteArrayInputStream("OUTPUT".toByteArray())))
        )

        mockMvc.perform(
            multipart("/api/printscript/execute")
                .file(snippet)
                .param("version", "2.0")
        )
            .andExpect(status().isOk)
            .andExpect(content().string("OUTPUT"))

        verify(printscriptService).execute(any<InputStream>(), any<String>())
    }

    @Test
    fun format_returns_formatted_code() {
        val snippet = MockMultipartFile(
            "snippet",
            "code.txt",
            "text/plain",
            "let x=5;".toByteArray()
        )
        val config = MockMultipartFile(
            "config",
            "format-config.json",
            "application/json",
            "{ \"enforce-spacing-around-equals\": true}".toByteArray()
        )

        `when`(printscriptService.format(any<InputStream>(), any<String>(), any<String>())).thenReturn(
            ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("let x = 5;")
        )

        mockMvc.perform(
            multipart("/api/printscript/format")
                .file(snippet)
                .file(config)
                .param("version", "1.0")
        )
            .andExpect(status().isOk)
            .andExpect(content().string("let x = 5;"))

        verify(printscriptService).format(any<InputStream>(), any<String>(), any<String>())
    }

    @Test
    fun verify_returns_validation_result() {
        val snippet = MockMultipartFile(
            "snippet",
            "code.txt",
            "text/plain",
            "let x: number = 5;".toByteArray()
        )
        val config = MockMultipartFile(
            "config",
            "verify-config.json",
            "application/json",
            "{\"identifier_format\": \"camel case\",}".toByteArray()
        )

        `when`(printscriptService.verify(any<InputStream>(), any<String>(), any<String>())).thenReturn(
            ResponseEntity.ok()
                .body("Code is valid")
        )

        mockMvc.perform(
            multipart("/api/printscript/verify")
                .file(snippet)
                .file(config)
                .param("version", "1.0")
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Code is valid"))

        verify(printscriptService).verify(any<InputStream>(), any<String>(), any<String>())
    }

    @Test
    fun verify_returns_bad_request_for_invalid_config() {
        val snippet = MockMultipartFile(
            "snippet",
            "code.txt",
            "text/plain",
            "let x = 5;".toByteArray()
        )
        val config = MockMultipartFile(
            "config",
            "config.txt",
            "text/plain",
            "not json".toByteArray()
        )

        mockMvc.perform(
            multipart("/api/printscript/verify")
                .file(snippet)
                .file(config)
                .param("version", "1.0")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("no configuration file was found"))
    }
}
