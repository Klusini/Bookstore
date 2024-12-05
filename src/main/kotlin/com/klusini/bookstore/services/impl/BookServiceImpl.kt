package com.klusini.bookstore.services.impl

import com.klusini.bookstore.domain.BookSummary
import com.klusini.bookstore.domain.entities.BookEntity
import com.klusini.bookstore.repositories.AuthorRepository
import com.klusini.bookstore.repositories.BookRepository
import com.klusini.bookstore.services.BookService
import com.klusini.bookstore.toBookEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookServiceImpl(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) :BookService {

    @Transactional
    override fun createUpdate(isbn: String, bookSummary: BookSummary): Pair<BookEntity, Boolean> {
        val normalisedBook = bookSummary.copy(isbn = isbn)
        val isExists = bookRepository.existsById(isbn)

        val author = authorRepository.findByIdOrNull(normalisedBook.author.id)
        checkNotNull(author)

        val savedBook = bookRepository.save(normalisedBook.toBookEntity(author))
        return Pair(savedBook, !isExists)
    }

    override fun list(authorId: Long?): List<BookEntity> {
        return authorId?.let {
            bookRepository.findByAuthorEntityId(it)
        } ?: bookRepository.findAll()
    }
}