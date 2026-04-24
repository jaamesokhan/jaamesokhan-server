package ir.jaamebaade.controller

import ir.jaamebaade.dto.RecitationDto
import ir.jaamebaade.model.Recitation
import ir.jaamebaade.repository.RecitationRepository
import ir.jaamebaade.service.RecitationService
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.lang.reflect.Proxy
import java.util.Optional

class RecitationControllerTest {
    private val recitationService = object : RecitationService(dummyRepository()) {
        override fun listByPoemId(poemId: Int): List<RecitationDto> {
            return listOf(
                RecitationDto(
                    audioId = 7,
                    artistName = "Test Artist",
                    poemId = poemId,
                    audioFileUrl = "https://cdn.example/audio.mp3",
                    syncFileUrl = "https://cdn.example/text.xml",
                )
            )
        }
    }
    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(RecitationController(recitationService))
        .build()

    @Test
    fun `listByPoemId returns recitations from service`() {
        mockMvc.perform(get("/api/v1/recitations/42"))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].audioId").value(7))
            .andExpect(jsonPath("$[0].artistName").value("Test Artist"))
            .andExpect(jsonPath("$[0].poemId").value(42))
            .andExpect(jsonPath("$[0].audioFileUrl").value("https://cdn.example/audio.mp3"))
            .andExpect(jsonPath("$[0].syncFileUrl").value("https://cdn.example/text.xml"))
    }

    private fun dummyRepository(): RecitationRepository {
        return Proxy.newProxyInstance(
            RecitationRepository::class.java.classLoader,
            arrayOf(RecitationRepository::class.java),
        ) { _, method, _ ->
            when (method.name) {
                "findByAudioId" -> Optional.empty<Recitation>()
                "findAllByPoemIdOrderByAudioId" -> emptyList<Recitation>()
                "save" -> null
                "count" -> 0L
                "existsById" -> false
                "findAll" -> emptyList<Recitation>()
                "findAllById" -> emptyList<Recitation>()
                "findById" -> Optional.empty<Recitation>()
                "deleteById", "delete", "deleteAllById", "deleteAll", "deleteAllInBatch", "deleteAllByIdInBatch" -> null
                "saveAll" -> emptyList<Recitation>()
                "hashCode" -> System.identityHashCode(this)
                "toString" -> "DummyRecitationRepository"
                "equals" -> false
                else -> throw UnsupportedOperationException("Unexpected call to ${method.name}")
            }
        } as RecitationRepository
    }
}
