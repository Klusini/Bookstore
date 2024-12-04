package com.klusini.bookstore.domain.dto

data class  BookSummaryDto(
    val isbn: String,
    val title: String,
    val description: String,
    val image: String,
    val authorSummaryDto: AuthorSummaryDto,
)
