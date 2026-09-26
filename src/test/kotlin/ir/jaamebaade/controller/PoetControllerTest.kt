package ir.jaamebaade.controller

import io.minio.MinioClient
import ir.jaamebaade.repository.PoetRepository
import ir.jaamebaade.service.PoetService
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.lang.reflect.Proxy

class PoetControllerTest {
    private val poetService = object : PoetService(dummyRepository(), MinioClient.builder().endpoint("http://localhost").build()) {
        override fun downloadPoet(poetId: Int): String = "https://cdn.example/poet_$poetId.zip?sig=abc"
    }
    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(PoetController(poetService))
        .build()

    @Test
    fun `download redirects to presigned url`() {
        mockMvc.perform(get("/api/v1/poet/download/2"))
            .andExpect(status().isFound)
            .andExpect(header().string("Location", "https://cdn.example/poet_2.zip?sig=abc"))
    }

    @Test
    fun `downloadUrl returns presigned url as json`() {
        mockMvc.perform(get("/api/v1/poet/download/2/url"))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.url").value("https://cdn.example/poet_2.zip?sig=abc"))
    }

    private fun dummyRepository(): PoetRepository {
        return Proxy.newProxyInstance(
            PoetRepository::class.java.classLoader,
            arrayOf(PoetRepository::class.java),
        ) { _, method, _ ->
            when (method.name) {
                "hashCode" -> System.identityHashCode(this)
                "toString" -> "DummyPoetRepository"
                "equals" -> false
                else -> throw UnsupportedOperationException("Unexpected call to ${method.name}")
            }
        } as PoetRepository
    }
}
