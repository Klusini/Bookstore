package com.klusini.bookstore.services

import com.klusini.bookstore.domain.BookSummary
import com.klusini.bookstore.domain.entities.BookEntity

interface BookService {
    fun createUpdate(isbn: String, bookSummary: BookSummary): Pair<BookEntity, Boolean>

    fun list(authorId: Long?=null): List<BookEntity>

    fun get(isbn: String): BookEntity?

}