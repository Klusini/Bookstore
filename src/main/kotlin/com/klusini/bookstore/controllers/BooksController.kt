package com.klusini.bookstore.controllers

import com.klusini.bookstore.domain.dto.BookSummaryDto
import com.klusini.bookstore.exceptions.InvalidAuthorException
import com.klusini.bookstore.services.BookService
import com.klusini.bookstore.toBookSummary
import com.klusini.bookstore.toBookSummaryDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/books")
class BooksController(private val bookService: BookService) {

    @PutMapping(path = ["/{isbn}"])
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

    @GetMapping
    fun readManyBooks(@RequestParam("author") authorId: Long?): List<BookSummaryDto>{
        return bookService.list(authorId).map { it.toBookSummaryDto() }
    }

   @GetMapping(path = ["/{isbn}"])
   fun readOneBook(@PathVariable("isbn") isbn: String): ResponseEntity<BookSummaryDto> {
       return bookService.get(isbn)?.let { ResponseEntity(it.toBookSummaryDto(), HttpStatus.OK) } ?: ResponseEntity(HttpStatus.NOT_FOUND)
   }

}