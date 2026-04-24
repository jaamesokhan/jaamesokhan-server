package ir.jaamebaade.repository

import ir.jaamebaade.model.Recitation
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface RecitationRepository : CrudRepository<Recitation, UUID> {
    fun findByAudioId(audioId: Int): Optional<Recitation>

    fun findAllByPoemIdOrderByAudioId(poemId: Int): List<Recitation>
}
