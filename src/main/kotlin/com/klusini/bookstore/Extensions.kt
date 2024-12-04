package com.klusini.bookstore

import com.klusini.bookstore.domain.AuthorSummary
import com.klusini.bookstore.domain.AuthorUpdateRequest
import com.klusini.bookstore.domain.BookSummary
import com.klusini.bookstore.domain.dto.AuthorDto
import com.klusini.bookstore.domain.dto.AuthorSummaryDto
import com.klusini.bookstore.domain.dto.AuthorUpdateRequestDto
import com.klusini.bookstore.domain.dto.BookSummaryDto
import com.klusini.bookstore.domain.entities.AuthorEntity
import com.klusini.bookstore.domain.entities.BookEntity
import com.klusini.bookstore.exceptions.InvalidAuthorException

fun AuthorEntity.toAuthorDto() = AuthorDto(
    id = this.id,
    name = this.name,
    age = this.age,
    description = this.description,
    image = this.image,
)
fun AuthorEntity.toAuthorSummaryDto():  AuthorSummaryDto {
    val authorId = this.id ?: throw InvalidAuthorException()
    return AuthorSummaryDto(
        id = authorId,
        name = this.name,
        image = this.image,
    )
}

fun AuthorDto.toAuthorEntity() = AuthorEntity(
    id = this.id,
    name = this.name,
    age = this.age,
    description = this.description,
    image = this.image,
)

fun AuthorSummaryDto.toAuthorSummary() = AuthorSummary(
    id = this.id,
    name = this.name,
    image = this.image
)

fun AuthorUpdateRequestDto.toAuthorUpdateRequest() = AuthorUpdateRequest(
    id = this.id,
    name = this.name,
    age = this.age,
    description = this.description,
    image = this.image,
)

fun BookSummary.toBookEntity(author: AuthorEntity) = BookEntity(
    isbn = this.isbn,
    title = this.title,
    description = this.description,
    image = this.image,
    authorEntity = author
)

fun BookSummaryDto.toBookSummary() = BookSummary(
    isbn = this.isbn,
    title = this.title,
    description = this.description,
    image = this.image,
    author = this.authorSummaryDto.toAuthorSummary()
)

fun BookEntity.toBookSummaryDto() = BookSummaryDto(
    isbn = this.isbn,
    title = this.title,
    description = this.description,
    image = this.image,
    authorSummaryDto = this.authorEntity.toAuthorSummaryDto()
)
