package com.klusini.bookstore

import com.klusini.bookstore.domain.dto.AuthorDto
import com.klusini.bookstore.domain.entities.AuthorEntity

fun testAuthorDtoA (id: Long? = null) = AuthorDto(
    id = id,
    name = "John Doe",
    age = 30,
    description = "Some description",
    image = "author-image.jpeg"
)

fun testAuthorEntityA (id: Long? = null) = AuthorEntity(
    id = id,
    name = "John Doe",
    age = 30,
    description = "Some description",
    image = "author-image.jpeg"
)