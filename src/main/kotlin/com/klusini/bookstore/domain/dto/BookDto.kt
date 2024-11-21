package com.klusini.bookstore.domain.dto

data class  BookDto(
    val isbn: String,
    val title: String,
    val description: String,
    val image: String,
    val authorDto: AuthorDto,

)
