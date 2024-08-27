package com.example.easybook

data class Livro(
    val id: Int,
    val titulo: String,
    val editora: String,
    val genero: String,
    val foto: ByteArray?, // Use `ByteArray?` para imagens que podem ser nulas
    val lido: Boolean,
    val nota: Int
)
