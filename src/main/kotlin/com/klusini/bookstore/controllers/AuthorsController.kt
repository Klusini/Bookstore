package com.klusini.bookstore.controllers

import com.klusini.bookstore.domain.dto.AuthorDto
import com.klusini.bookstore.services.AuthorService
import com.klusini.bookstore.toAuthorDto
import com.klusini.bookstore.toAuthorEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/v1/authors"])
class AuthorsController(private val authorService: AuthorService){

    @PostMapping
    fun createAuthor(@RequestBody authorEntity: AuthorDto) : ResponseEntity<AuthorDto> {
        val createdAuthor =  authorService.save(
            authorEntity.toAuthorEntity()
        ).toAuthorDto()
        return ResponseEntity(createdAuthor, HttpStatus.CREATED)
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

}