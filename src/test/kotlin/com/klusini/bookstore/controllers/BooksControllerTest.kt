package com.klusini.bookstore.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.klusini.bookstore.*
import com.klusini.bookstore.services.BookService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.result.StatusResultMatchersDsl

@SpringBootTest
@AutoConfigureMockMvc
class BooksControllerTest @Autowired constructor(
    private val mockMvc: MockMvc, @MockkBean val bookService: BookService
) {
    val objectMapper = ObjectMapper()

    @Test
    fun `test that createFullUpdateBook returns HTTP 201 when book is created`() {
       assertThatUserCreatedUpdated(true) {isCreated()}
    }

    @Test
    fun `test that createFullUpdateBook returns HTTP 200 when book is updated`() {
        assertThatUserCreatedUpdated(false) {isOk()}
    }

    private fun assertThatUserCreatedUpdated(isCreated: Boolean, statusCodeAssertion: StatusResultMatchersDsl.() -> Unit) {
        val isbn = "978-040-646163-5346"
        val author = testAuthorEntityA(1)
        val savedBook = testBookEntityA(isbn, author)

        val authorSummaryDto = testAuthorSummaryDtoA(1)
        val bookSummaryDto = testBookSummaryDtoA(isbn, authorSummaryDto)

        every {
            bookService.createUpdate(isbn, any())
        } answers {
            Pair(savedBook, isCreated)
        }

        mockMvc.put("/v1/books/$isbn") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookSummaryDto)
        }.andExpect {
            status { statusCodeAssertion() }
        }
    }

    @Test
    fun `test that createFullUpdateBook returns HTTP 500 when author in the database doesnt have an ID`() {
        val isbn = "978-040-646163-5346"
        val author = testAuthorEntityA()
        val savedBook = testBookEntityA(isbn, author)

        val authorSummaryDto = testAuthorSummaryDtoA(1)
        val bookSummaryDto = testBookSummaryDtoA(isbn, authorSummaryDto)

        every {
            bookService.createUpdate(isbn, any())
        } answers {
            Pair(savedBook, true)
        }

        mockMvc.put("/v1/books/$isbn") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookSummaryDto)
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun `test that createFullUpdateBook returns HTTP 400 when author does not exist`() {
        val isbn = "978-040-646163-5346"

        val authorSummaryDto = testAuthorSummaryDtoA(1)
        val bookSummaryDto = testBookSummaryDtoA(isbn, authorSummaryDto)

        every {
            bookService.createUpdate(isbn, any())
        } throws IllegalStateException()

        mockMvc.put("/v1/books/$isbn") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookSummaryDto)
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun `test that readManyBooks returns a list of books`(){
        val isbn = "978-040-646163-5346"

        every {
            bookService.list()
        } answers {
            listOf(testBookEntityA(isbn, testAuthorEntityA(1)))
        }

        mockMvc.get("/v1/books"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$[0].isbn", equalTo(isbn)) }
            content { jsonPath("$[0].title", equalTo("Test Book A")) }
            content { jsonPath("$[0].image", equalTo("book-image.jpeg")) }
            content { jsonPath("$[0].author.id", equalTo(1)) }
            content { jsonPath("$[0].author.name", equalTo("John Doe")) }
            content { jsonPath("$[0].author.image", equalTo("author-image.jpeg")) }
        }
    }

    @Test
    fun `test that list returns no books when they do not match the author ID`(){
        every {
            bookService.list(any())
        } answers {
            emptyList()
        }

        mockMvc.get("/v1/books?author=999"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { json("[]")}
        }
    }

    @Test
    fun `test that list returns a book when matches author ID`(){
        val isbn = "978-040-646163-5346"

        every {
            bookService.list(1L)
        } answers {
            listOf(testBookEntityA(isbn, testAuthorEntityA(1L)))
        }

        mockMvc.get("/v1/books?author=1"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$[0].isbn", equalTo(isbn)) }
            content { jsonPath("$[0].title", equalTo("Test Book A")) }
            content { jsonPath("$[0].image", equalTo("book-image.jpeg")) }
            content { jsonPath("$[0].author.id", equalTo(1)) }
            content { jsonPath("$[0].author.name", equalTo("John Doe")) }
            content { jsonPath("$[0].author.image", equalTo("author-image.jpeg")) }
        }
    }

    @Test
    fun `test that readOneBook returns HTTP 404 when no book found`(){
        val isbn = "978-040-646163-5346"

        every {
            bookService.get(any())
        } answers {
            null
        }

        mockMvc.get("/v1/books/${isbn}") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

    }

    @Test
    fun `test that readOneBook returns HTTP 200 and a book when found`(){
        val isbn = "978-040-646163-5346"

        every {
            bookService.get(isbn)
        } answers {
            testBookEntityA(isbn, testAuthorEntityA(1))
        }

        mockMvc.get("/v1/books/${isbn}") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.isbn", equalTo(isbn)) }
            content { jsonPath("$.title", equalTo("Test Book A")) }
            content { jsonPath("$.image", equalTo("book-image.jpeg")) }
            content { jsonPath("$.author.id", equalTo(1)) }
            content { jsonPath("$.author.name", equalTo("John Doe")) }
            content { jsonPath("$.author.image", equalTo("author-image.jpeg")) }
        }

    }

}