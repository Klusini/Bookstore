package com.klusini.bookstore.services.impl


import com.klusini.bookstore.*
import com.klusini.bookstore.domain.AuthorSummary
import com.klusini.bookstore.repositories.AuthorRepository
import com.klusini.bookstore.repositories.BookRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@Transactional
class BookServiceImplTest@Autowired constructor(
    private val underTest: BookServiceImpl,
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) {

    @Test
    fun `test that createUpdate throws an IllegalStateException if author does not exist`() {
        val authorSummary = testAuthorSummaryA(id = 999L)
        val bookSummary = testBookSummaryA(BOOK_A_ISBN, authorSummary)
        assertThrows<IllegalStateException> {
            underTest.createUpdate(BOOK_A_ISBN, bookSummary)
        }
    }

    @Test
    fun `test that createUpdate successfully creates a book in the database`() {
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull

        val authorSummary = testAuthorSummaryA(id = savedAuthor.id!!)
        val bookSummary = testBookSummaryA(BOOK_A_ISBN, authorSummary)

        val (savedBook,isCreated) = underTest.createUpdate(BOOK_A_ISBN, bookSummary)
        assertThat(savedBook).isNotNull

        val recalledBook = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertThat(recalledBook).isNotNull()
        assertThat(recalledBook).isEqualTo(savedBook)
        assertThat(isCreated).isTrue

    }

    @Test
    fun `test that createUpdate successfully updates a book in the database`() {
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN,savedAuthor))
        assertThat(savedBook).isNotNull

        val authorSummary = AuthorSummary(id = savedAuthor.id!!)
        val bookSummary = testBookSummaryB(BOOK_A_ISBN, authorSummary)

        val (updatedBook,isCreated) = underTest.createUpdate(BOOK_A_ISBN, bookSummary)
        assertThat(updatedBook).isNotNull

        val recalledBook = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertThat(recalledBook).isNotNull
        assertThat(isCreated).isFalse
    }

    @Test
    fun `test that list returns empty list when no books in the database`() {
        val result = underTest.list()
        assertThat(result).isEmpty()
    }

    @Test
    fun `test that list returns list of books if present in the database`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN,savedAuthor))
        assertThat(savedBook).isNotNull

        val result = underTest.list()
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(savedBook)
    }

    @Test
    fun `test that list returns no books when the author ID doesnt match`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN,savedAuthor))
        assertThat(savedBook).isNotNull

        val result = underTest.list(authorId = savedAuthor.id!! + 1)
        assertThat(result).hasSize(0)
    }

    @Test
    fun `test that list returns books when the author ID matches`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN,savedAuthor))
        assertThat(savedBook).isNotNull

        val result = underTest.list(authorId = savedAuthor.id)
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(savedBook)
    }

    @Test
    fun `test that get returns null when book not found in the database`() {
        val result = underTest.get(BOOK_A_ISBN)
        assertThat(result).isNull()
    }

    @Test
    fun `test that returns a book when found in the database`() {
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN,savedAuthor))
        assertThat(savedBook).isNotNull

        val result = underTest.get(BOOK_A_ISBN)
        assertThat(result).isEqualTo(savedBook)
    }

}