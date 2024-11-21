package com.klusini.bookstore.controllers

import com.klusini.bookstore.domain.dto.AuthorDto
import com.klusini.bookstore.services.AuthorService
import com.klusini.bookstore.toAuthorDto
import com.klusini.bookstore.toAuthorEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorsController(private val authorService: AuthorService){

    @PostMapping(path = ["v1/authors"])
    fun createAuthor(@RequestBody authorEntity: AuthorDto) : AuthorDto {
        return authorService.save(
            authorEntity.toAuthorEntity()
        ).toAuthorDto()
    }

}