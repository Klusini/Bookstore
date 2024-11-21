package com.klusini.bookstore.services.impl

import com.klusini.bookstore.domain.entities.AuthorEntity
import com.klusini.bookstore.repositories.AuthorRepository
import com.klusini.bookstore.services.AuthorService
import org.springframework.stereotype.Service

@Service
class AuthorServiceImpl(private val authorRepository: AuthorRepository ) : AuthorService {

    override fun save(authorEntity: AuthorEntity): AuthorEntity {
        return authorRepository.save(authorEntity)
    }

}