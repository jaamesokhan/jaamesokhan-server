package ir.jaamebaade.service

import ir.jaamebaade.model.Recitation
import ir.jaamebaade.repository.RecitationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.reflect.Proxy
import java.util.Optional

class RecitationServiceTest {
    private val recitationRepository = fakeRepository()
    private val recitationService = RecitationService(recitationRepository)

    @Test
    fun `listByPoemId maps repository models to dto`() {
        val result = recitationService.listByPoemId(99)

        assertEquals(2, result.size)
        assertEquals(1, result[0].audioId)
        assertEquals("First Artist", result[0].artistName)
        assertEquals(99, result[0].poemId)
        assertEquals("https://cdn.example/1.mp3", result[0].audioFileUrl)
        assertEquals(null, result[0].syncFileUrl)
        assertEquals(2, result[1].audioId)
        assertEquals("Second Artist", result[1].artistName)
        assertEquals(99, result[1].poemId)
        assertEquals(null, result[1].audioFileUrl)
        assertEquals("https://cdn.example/2.xml", result[1].syncFileUrl)
    }

    private fun fakeRepository(): RecitationRepository {
        return Proxy.newProxyInstance(
            RecitationRepository::class.java.classLoader,
            arrayOf(RecitationRepository::class.java),
        ) { _, method, args ->
            when (method.name) {
                "findAllByPoemIdOrderByAudioId" -> {
                    val poemId = args?.first() as Int
                    listOf(
                        Recitation(
                            audioId = 1,
                            artistName = "First Artist",
                            poemId = poemId,
                            audioFileUrl = "https://cdn.example/1.mp3",
                            syncFileUrl = null,
                        ),
                        Recitation(
                            audioId = 2,
                            artistName = "Second Artist",
                            poemId = poemId,
                            audioFileUrl = null,
                            syncFileUrl = "https://cdn.example/2.xml",
                        )
                    )
                }
                "findByAudioId" -> Optional.empty<Recitation>()
                "save" -> args?.first()
                "count" -> 0L
                "existsById" -> false
                "findAll" -> emptyList<Recitation>()
                "findAllById" -> emptyList<Recitation>()
                "findById" -> Optional.empty<Recitation>()
                "deleteById", "delete", "deleteAllById", "deleteAll", "deleteAllInBatch", "deleteAllByIdInBatch" -> null
                "saveAll" -> args?.first() ?: emptyList<Recitation>()
                "hashCode" -> System.identityHashCode(this)
                "toString" -> "FakeRecitationRepository"
                "equals" -> false
                else -> throw UnsupportedOperationException("Unexpected call to ${method.name}")
            }
        } as RecitationRepository
    }
}
