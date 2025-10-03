package com.ingsis.grupo10.printscript.printscript.dto

data class LintErrorDTO(
    val message: String,
    val type: String,
    val segment: Int? = null,
)
