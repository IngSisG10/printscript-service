package com.ingsis.grupo10.printscript.printscript.controller

import com.ingsis.grupo10.printscript.printscript.service.PrintscriptService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@SpringBootApplication
@RestController
@RequestMapping("/api/printscript")
class PrintscriptController(
    private val printscriptService: PrintscriptService,
) {
    private fun configContent(config: MultipartFile): String? {
        val filename = config.originalFilename ?: ""
        if (!filename.endsWith(".json")) {
            return null
        }
        return config.inputStream.bufferedReader().use { it.readText() }
    }

    @PostMapping(
        "/execute",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.TEXT_PLAIN_VALUE],
    )
    fun execute(
        @RequestPart("snippet") snippet: MultipartFile,
        @RequestParam(value = "version", required = false) version: String?,
    ): ResponseEntity<InputStreamResource> = printscriptService.execute(snippet.inputStream, version ?: "1.0")

    @PostMapping(
        "/verify",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.TEXT_PLAIN_VALUE],
    )
    fun verify(
        @RequestParam(value = "version", required = false) version: String?,
        @RequestPart("snippet") snippet: MultipartFile,
        @RequestPart("config") config: MultipartFile,
    ): ResponseEntity<out Any?> {
        val configContent =
            configContent(config) ?: return ResponseEntity.badRequest().body("no configuration file was found")
        return printscriptService.verify(snippet.inputStream, configContent, version ?: "1.0")
    }

    @GetMapping(
        "/format",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.TEXT_PLAIN_VALUE],
    )
    fun format(
        @RequestParam(value = "version", required = false) version: String?,
        @RequestPart("snippet") snippet: MultipartFile,
        @RequestPart("config") config: MultipartFile,
    ): ResponseEntity<out Any?> {
        val configContent =
            configContent(config) ?: return ResponseEntity.badRequest().body("no configuration file was found")
        return printscriptService.format(snippet.inputStream, configContent, version ?: "1.0")
    }
}
