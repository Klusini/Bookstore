package com.klusini.bookstore.services.impl

import com.klusini.bookstore.repositories.AuthorRepository
import com.klusini.bookstore.testAuthorEntityA
import com.klusini.bookstore.testAuthorEntityB
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class AuthorServiceImplTest@Autowired constructor(
    val underTest: AuthorServiceImpl,
    val authorRepository: AuthorRepository
) {

    @Test
    fun `test that save persists the Author in the database`() {
        val savedAuthor = underTest.create(testAuthorEntityA())
        assertThat(savedAuthor.id).isNotNull()

        val recalledAuthor = authorRepository.findByIdOrNull(savedAuthor.id!!)
        assertThat(recalledAuthor).isNotNull()
        assertThat(recalledAuthor!!).isEqualTo(
            testAuthorEntityA(id = savedAuthor.id))
    }

    @Test
    fun `test that author with an ID throws an IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            val existingAuthor = testAuthorEntityA(999)
            underTest.create(existingAuthor)
        }
    }

    @Test
    fun `test that list returns empty list when no authors in the database`() {
        val result = underTest.list()
        assertThat(result).isEmpty()
    }

    @Test
    fun `test that list returns authors when authors present in the database`() {
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val expected = listOf(savedAuthor)
        val result = underTest.list()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `test that get returns null when author not found in database`(){
        val result = underTest.get(999)
        assertThat(result).isNull()
    }

    @Test
    fun `test that get returns author when author present in the database`() {
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val result = underTest.get(savedAuthor.id!!)
        assertThat(result).isEqualTo(savedAuthor)
        authorRepository.delete(savedAuthor)
    }

    @Test
    fun `test that fullUpdate successfully updates the author in the database`() {
        val existingAuthor = authorRepository.save(testAuthorEntityA())
        val existingAuthorId = existingAuthor.id!!
        val updatedAuthor =  testAuthorEntityB(existingAuthorId)
        val result = underTest.fullUpdate(existingAuthorId, updatedAuthor)
        assertThat(result).isEqualTo(updatedAuthor)

        val retrievedAuthor = authorRepository.findByIdOrNull(existingAuthorId)
        assertThat(retrievedAuthor).isNotNull()
        assertThat(retrievedAuthor).isEqualTo(updatedAuthor)
    }

    @Test
    fun `test that fullUpdate throws IllegalStateException when author not found in database`(){
        assertThrows<IllegalStateException> {
            val nonExistingAuthorId = 999L
            val updatedAuthor = testAuthorEntityB(nonExistingAuthorId)
            underTest.fullUpdate(nonExistingAuthorId, updatedAuthor)
        }
    }
}