package com.ingsis.grupo10.printscript.health.controller

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
@RequestMapping("/health")
class Health {

    @GetMapping
    fun health(): ResponseEntity<HttpStatus> {
        return ResponseEntity.ok().build()
    }
}