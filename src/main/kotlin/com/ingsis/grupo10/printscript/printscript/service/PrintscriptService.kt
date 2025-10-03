package com.ingsis.grupo10.printscript.printscript.service

import com.ingsis.grupo10.printscript.printscript.dto.LintErrorDTO
import com.ingsis.grupo10.printscript.printscript.dto.LintResultDTO
import com.ingsis.grupo10.printscript.printscript.dto.RuntimeErrorDTO
import common.util.segmentsBySemicolon
import common.util.segmentsBySemicolonPreserveWhitespace
import formatter.util.FormatterUtil
import interpreter.Interpreter
import lexer.util.LexerUtil.Companion.createLexer
import linter.util.LinterUtil.Companion.createLinter
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import parser.Parser
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.collections.forEach
import kotlin.sequences.forEach

@Service
class PrintscriptService {
    fun execute(
        fileText: InputStream,
        version: String,
    ): ResponseEntity<InputStreamResource> =
        try {
            val lexer = createLexer(version)
            val parser = Parser()
            val interpreter = Interpreter()

            val outputBuilder = StringBuilder()

            fileText.segmentsBySemicolon().forEach { segment ->
                val tokens = lexer.lex(segment)
                val ast = parser.parse(tokens)
                val output = interpreter.interpret(ast)
                output.forEach { line ->
                    outputBuilder.appendLine(line)
                }
            }

            // Convertir el resultado a InputStream
            val inputStream = ByteArrayInputStream(outputBuilder.toString().toByteArray())

            ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=result.txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(InputStreamResource(inputStream))
        } catch (t: Throwable) {
            val errorStream = ByteArrayInputStream("Error: ${t.message}".toByteArray())
            ResponseEntity
                .badRequest()
                .contentType(MediaType.TEXT_PLAIN)
                .body(InputStreamResource(errorStream))
        }

    fun verify(
        fileText: InputStream,
        configText: String,
        version: String,
    ): ResponseEntity<Any> {
        val lexer = createLexer(version)
        val linter = createLinter(configText, version)

        val allErrors = mutableListOf<LintErrorDTO>()
        var segIndex = 0
        var sawAnySegment = false

        fileText.segmentsBySemicolon().forEach { segment ->
            val trimmed = segment.trim()
            if (trimmed.isEmpty()) return@forEach

            sawAnySegment = true
            segIndex++

            try {
                val tokens = lexer.lex(trimmed)
                val lintErrors: List<Throwable> = linter.lint(tokens)
                lintErrors.forEach { t ->
                    allErrors +=
                        LintErrorDTO(
                            message = t.message ?: t.toString(),
                            type = t::class.simpleName ?: "Throwable",
                            segment = segIndex,
                        )
                }
            } catch (t: Throwable) {
                // ⚠️ Esto es error de ejecución, NO lint → respondemos 400 inmediatamente
                return ResponseEntity
                    .badRequest()
                    .body(RuntimeErrorDTO(error = t.message ?: t.toString()))
            }
        }

        if (!sawAnySegment) {
            // request válido pero sin contenido útil
            return ResponseEntity.ok(LintResultDTO(errors = emptyList()))
        }

        // 200 siempre para resultados de lint (con o sin findings)
        return ResponseEntity.ok(LintResultDTO(errors = allErrors))
    }

    fun format(
        fileText: InputStream,
        configText: String,
        version: String,
    ): ResponseEntity<out Any?> {
        val lexer = createLexer(version)
        val formatter = FormatterUtil.createFormatter(configText, version)
        fileText.segmentsBySemicolonPreserveWhitespace().forEach { segment ->
            return try {
                val outputBuilder = StringBuilder()
                val tokens = lexer.lex(segment)
                val output = formatter.format(tokens)
                output.forEach { line ->
                    outputBuilder.appendLine(line)
                }

                val inputStream = ByteArrayInputStream(outputBuilder.toString().toByteArray())

                ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=result.txt")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(InputStreamResource(inputStream))
            } catch (t: Throwable) {
                val errorStream = ByteArrayInputStream("Error: ${t.message}".toByteArray())
                ResponseEntity
                    .badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(InputStreamResource(errorStream))
            }
        }
        return ResponseEntity.badRequest().body("No segments were sent")
    }
}
