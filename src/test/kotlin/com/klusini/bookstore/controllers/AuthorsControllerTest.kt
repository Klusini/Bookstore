package com.klusini.bookstore.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.klusini.bookstore.domain.entities.AuthorEntity
import com.klusini.bookstore.services.AuthorService
import com.klusini.bookstore.testAuthorDtoA
import com.klusini.bookstore.testAuthorEntityA
import com.klusini.bookstore.testAuthorUpdateRequestDtoA
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

private const val AUTHORS_BASE_URL = "/v1/authors"

@SpringBootTest
@AutoConfigureMockMvc
class AuthorsControllerTest @Autowired constructor (
    private val mockMvc: MockMvc,
    @MockkBean val authorService: AuthorService
) {

    @BeforeEach
    fun beforeEach() {
        every {
            authorService.create(any())
        } answers {
            firstArg()
        }
        
    }

    val objectMapper = ObjectMapper()

    @Test
    fun `test that create Author saves the Author`() {

        mockMvc.post(AUTHORS_BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
            content = objectMapper.writeValueAsString(
                testAuthorDtoA()
            )
        }
        val expected = AuthorEntity(
            id = null,
            name = "John Doe",
            age = 30,
            image = "author-image.jpeg",
            description = "Some description"
        )
        verify { authorService.create(expected) }
    }

    @Test
    fun `test that create author returns s HTTP 201 status on a successful create`(){
        mockMvc.post(AUTHORS_BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
            content = objectMapper.writeValueAsString(
                testAuthorDtoA()
            )
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `test that create author returns HTTP 400 when IllegalArgumentException is thrown`() {
        every {
            authorService.create(any())
        } throws (IllegalArgumentException())

        mockMvc.post(AUTHORS_BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
            content = objectMapper.writeValueAsString(
                testAuthorDtoA()
            )
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `test that list returns an empty list and HTTP 200 when no authors in the database`(){
        every {
            authorService.list()
        } answers {
            emptyList()
        }

        mockMvc.get(AUTHORS_BASE_URL){
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { json("[]") }
        }
    }

    @Test
    fun `test that return authors and HTTP 200 when authors in the database`(){
        every {
            authorService.list()
        } answers {
            listOf(testAuthorEntityA(1))
        }

        mockMvc.get(AUTHORS_BASE_URL){
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", equalTo(1)) }
            content { jsonPath("$[0].name", equalTo("John Doe")) }
            content { jsonPath("$[0].age", equalTo(30)) }
            content { jsonPath("$[0].description", equalTo("Some description")) }
            content { jsonPath("$[0].image", equalTo("author-image.jpeg")) }
        }
    }

    @Test
    fun `test that get returns HTTP 404 when author not found in Database`() {
        every {
            authorService.get(any())
        } answers {
            null
        }

        mockMvc.get("$AUTHORS_BASE_URL/999"){
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `test that get returns HTTP 200 and author when author found in database`(){
        every {
            authorService.get(any())
        } answers {
            testAuthorEntityA(999)
        }
        mockMvc.get("$AUTHORS_BASE_URL/999"){
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", equalTo(999)) }
            content { jsonPath("$.name", equalTo("John Doe")) }
            content { jsonPath("$.age", equalTo(30)) }
            content { jsonPath("$.description", equalTo("Some description")) }
            content { jsonPath("$.image", equalTo("author-image.jpeg")) }
        }
    }

    @Test
    fun `test that fullUpdate returns HTTP 200 and updated author on successful call`(){
        every {
            authorService.fullUpdate(any(), any())
        } answers {
            secondArg()
        }

        mockMvc.put("$AUTHORS_BASE_URL/999"){
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
            content = objectMapper.writeValueAsString(testAuthorDtoA(999))
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", equalTo(999)) }
            content { jsonPath("$.name", equalTo("John Doe")) }
            content { jsonPath("$.age", equalTo(30)) }
            content { jsonPath("$.description", equalTo("Some description")) }
            content { jsonPath("$.image", equalTo("author-image.jpeg")) }
        }
    }

    @Test
    fun `test that fullUpdate returns HTTP 400 on IllegalStateException`(){
        every {
            authorService.fullUpdate(any(), any())
        } throws (IllegalStateException())

        mockMvc.put("$AUTHORS_BASE_URL/999"){
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
            content = objectMapper.writeValueAsString(testAuthorDtoA(999))
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `test that partialUpdate returns HTTP 400 on IllegalStateException`(){
        every {
            authorService.partialUpdate(any(), any())
        } throws (IllegalStateException())

        mockMvc.patch("${AUTHORS_BASE_URL}/999"){
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
            content = objectMapper.writeValueAsString(
                testAuthorUpdateRequestDtoA(999)
            )
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `test that partialUpdate Author returns HTTP 200 and updated author if author present in the database`(){
        every {
            authorService.partialUpdate(any(), any())
        } answers {
            testAuthorEntityA(999)
        }

        mockMvc.patch("$AUTHORS_BASE_URL/999"){
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
            content = objectMapper.writeValueAsString(
                testAuthorUpdateRequestDtoA(999)
            )
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", equalTo(999)) }
            content { jsonPath("$.name", equalTo("John Doe")) }
            content { jsonPath("$.age", equalTo(30)) }
            content { jsonPath("$.description", equalTo("Some description")) }
            content { jsonPath("$.image", equalTo("author-image.jpeg")) }
        }
    }

    @Test
    fun `test that delete return HTTP 204 and deletes the author if present in the database`(){
        every {
            authorService.delete(any())
        } answers {}

        mockMvc.delete("$AUTHORS_BASE_URL/999"){
            contentType = MediaType.APPLICATION_JSON
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isNoContent() }
        }
    }

}