package ir.jaamebaade.repository

import ir.jaamebaade.model.Word
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WordRepository : CrudRepository<Word, Int> {
    fun findAllByName(name: String): List<Word>
}