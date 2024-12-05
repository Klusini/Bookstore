package com.klusini.bookstore.controllers

import com.klusini.bookstore.domain.dto.BookSummaryDto
import com.klusini.bookstore.exceptions.InvalidAuthorException
import com.klusini.bookstore.services.BookService
import com.klusini.bookstore.toBookSummary
import com.klusini.bookstore.toBookSummaryDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BooksController(private val bookService: BookService) {

    @PutMapping(path = ["/v1/books/{isbn}"])
    fun createFullUpdateBook(
        @PathVariable("isbn") isbn: String,
        @RequestBody book: BookSummaryDto
    ): ResponseEntity<BookSummaryDto> {
        return try {
            val (savedBook, isCreated) = bookService.createUpdate(isbn, book.toBookSummary())
            val responseCode = if (isCreated) HttpStatus.CREATED else HttpStatus.OK

            ResponseEntity(savedBook.toBookSummaryDto(), responseCode)

        } catch (ex: InvalidAuthorException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (ex: IllegalStateException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping(path = ["/v1/books"])
    fun readManyBooks(@RequestParam("author") authorId: Long?): List<BookSummaryDto>{
        return bookService.list(authorId).map { it.toBookSummaryDto() }
    }

}