package com.klusini.bookstore.services.impl

import com.klusini.bookstore.domain.AuthorUpdateRequest
import com.klusini.bookstore.domain.entities.AuthorEntity
import com.klusini.bookstore.repositories.AuthorRepository
import com.klusini.bookstore.testAuthorEntityA
import com.klusini.bookstore.testAuthorEntityB
import com.klusini.bookstore.testAuthorUpdateRequestA
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
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

    @Test
    fun `test that partialUpdate author throws IllegalStateException when author not found in database`(){
        assertThrows<IllegalStateException> {
            val nonExistingAuthorId = 999L
            val updateRequest = testAuthorUpdateRequestA(nonExistingAuthorId)
            underTest.partialUpdate(nonExistingAuthorId, updateRequest)
        }
    }

    @Test
    fun `test that partialUpdate values are all null and doesnt update the author`(){
        val existingAuthor = authorRepository.save(testAuthorEntityA(999))
        val updatedAuthor = underTest.partialUpdate(existingAuthor.id!!, AuthorUpdateRequest())
        assertThat(updatedAuthor).isEqualTo(existingAuthor)
    }

    @Test
    fun `test that partialUpdate Author updates author name`() {
        val existingAuthor = testAuthorEntityA()
        val newName = "New name"
        val expectedAuthor = existingAuthor.copy(
            name = newName
        )
        val authorUpdateRequest = AuthorUpdateRequest(
            name = newName
        )

        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor = expectedAuthor,
            authorUpdateRequest = authorUpdateRequest
        )
    }

    @Test
    fun `test that partialUpdate Author updates author age`() {
        val existingAuthor = testAuthorEntityA()
        val newAge = 41
        val expectedAuthor = existingAuthor.copy(
            age = newAge
        )
        val authorUpdateRequest = AuthorUpdateRequest(
            age = newAge
        )

        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor = expectedAuthor,
            authorUpdateRequest = authorUpdateRequest
        )
    }

    @Test
    fun `test that partialUpdate Author updates author description`() {
        val existingAuthor = testAuthorEntityA()
        val newDescription = "New description"
        val expectedAuthor = existingAuthor.copy(
            description = newDescription
        )
        val authorUpdateRequest = AuthorUpdateRequest(
            description = newDescription
        )

        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor = expectedAuthor,
            authorUpdateRequest = authorUpdateRequest
        )
    }

    @Test
    fun `test that partialUpdate Author updates author image`() {
        val existingAuthor = testAuthorEntityA()
        val newImage = "New-image.jpeg"
        val expectedAuthor = existingAuthor.copy(
            image = newImage
        )
        val authorUpdateRequest = AuthorUpdateRequest(
            image = newImage
        )

        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor = expectedAuthor,
            authorUpdateRequest = authorUpdateRequest
        )
    }

    private fun assertThatAuthorPartialUpdateIsUpdated(
        existingAuthor: AuthorEntity,
        expectedAuthor: AuthorEntity,
        authorUpdateRequest: AuthorUpdateRequest
    ) {
        //Save an existing Author
        val savedExistingAuthor = authorRepository.save(existingAuthor)
        val existingAuthorId = savedExistingAuthor.id!!

        //Update the Author
        val updatedAuthor = underTest.partialUpdate(
            existingAuthorId, authorUpdateRequest)

        //Set up the expected Author
        val expected = expectedAuthor.copy(id=existingAuthorId)
        assertThat(updatedAuthor).isEqualTo(expected)

        val retrievedAuthor = authorRepository.findByIdOrNull(existingAuthorId)
        assertThat(retrievedAuthor).isNotNull
        assertThat(retrievedAuthor).isEqualTo(expected)

    }

    @Test
    fun `test that delete deletes an existing Author in the database`(){
        val existingAuthor = authorRepository.save(testAuthorEntityA())
        val existingAuthorId = existingAuthor.id!!
        underTest.delete(existingAuthorId)
        assertThat(
            authorRepository.existsById(existingAuthorId)
        ).isFalse()
    }

    @Test
    fun `test that delete deletes an non existing Author in the database`(){
        val nonExistingAuthorId = 999L
        underTest.delete(nonExistingAuthorId)
        assertThat(
            authorRepository.existsById(nonExistingAuthorId)
        ).isFalse()
    }

}