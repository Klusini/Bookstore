package com.klusini.bookstore.services.impl

import com.klusini.bookstore.repositories.AuthorRepository
import com.klusini.bookstore.testAuthorEntityA
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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
        authorRepository.delete(savedAuthor)
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

}