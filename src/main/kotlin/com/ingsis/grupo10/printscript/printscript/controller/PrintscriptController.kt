package com.ingsis.grupo10.printscript.printscript.controller

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
@RequestMapping("/api/printscript")
class PrintscriptController {

    @GetMapping("/execute")
    fun execute(): ResponseEntity<HttpStatus> {
        return ResponseEntity.ok().build()
    }

    @GetMapping("/verify")
    fun verify(): ResponseEntity<HttpStatus> {
        return ResponseEntity.ok().build()
    }

    @GetMapping("/format")
    fun format(): ResponseEntity<HttpStatus> {
        return ResponseEntity.ok().build()
    }

}