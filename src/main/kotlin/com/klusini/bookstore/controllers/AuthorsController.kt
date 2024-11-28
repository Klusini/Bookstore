package com.klusini.bookstore.controllers

import com.klusini.bookstore.domain.dto.AuthorDto
import com.klusini.bookstore.domain.dto.AuthorUpdateRequestDto
import com.klusini.bookstore.services.AuthorService
import com.klusini.bookstore.toAuthorDto
import com.klusini.bookstore.toAuthorEntity
import com.klusini.bookstore.toAuthorUpdateRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/v1/authors"])
class AuthorsController(private val authorService: AuthorService){

    @PostMapping
    fun createAuthor(@RequestBody authorEntity: AuthorDto) : ResponseEntity<AuthorDto> {
        return try {
            val createdAuthor = authorService.create(
                authorEntity.toAuthorEntity()
            ).toAuthorDto()
            ResponseEntity(createdAuthor, HttpStatus.CREATED)
        } catch (ex: IllegalArgumentException){
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping
    fun readManyAuthors(): List<AuthorDto> {
        return authorService.list().map { it.toAuthorDto() }
    }

    @GetMapping(path = ["/{id}"])
    fun readyOneAuthor(@PathVariable("id") id: Long): ResponseEntity<AuthorDto> {
        val foundAuthor = authorService.get(id)?.toAuthorDto()
        return foundAuthor?.let {
            ResponseEntity(it, HttpStatus.OK)
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PutMapping(path = ["/{id}"])
    fun fullUpdateAuthor(@PathVariable("id") id: Long, @RequestBody authorDto: AuthorDto): ResponseEntity<AuthorDto> {
        return try {
            val updatedAuthor = authorService.fullUpdate(id, authorDto.toAuthorEntity())
            ResponseEntity(updatedAuthor.toAuthorDto(), HttpStatus.OK)
        } catch (ex: IllegalStateException){
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @PatchMapping(path = ["/{id}"])
    fun partialUpdateAuthor(@PathVariable("id") id: Long, @RequestBody authorUpdateRequest: AuthorUpdateRequestDto): ResponseEntity<AuthorDto> {
        return try{
            val updatedAuthor = authorService.partialUpdate(id, authorUpdateRequest.toAuthorUpdateRequest())
            ResponseEntity(updatedAuthor.toAuthorDto(), HttpStatus.OK)
        } catch (ex: IllegalStateException){
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

}