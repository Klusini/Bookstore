package com.klusini.bookstore.services

import com.klusini.bookstore.domain.entities.AuthorEntity

interface AuthorService {

    fun save(authorEntity: AuthorEntity) : AuthorEntity

}