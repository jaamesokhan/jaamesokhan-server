package ir.jaamebaade.configuration

import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CorsConfigurationTest {

    private fun requestTo(path: String) = MockHttpServletRequest("GET", path).apply { requestURI = path }

    @Test
    fun `wildcard allows any origin on api paths`() {
        val source = CorsConfiguration("*").corsConfigurationSource()
        val config = assertNotNull(source.getCorsConfiguration(requestTo("/api/v1/poet")))
        assertEquals("https://pwa.example", config.checkOrigin("https://pwa.example"))
        assertEquals(listOf("GET", "POST", "OPTIONS"), config.checkHttpMethod(org.springframework.http.HttpMethod.POST)?.map { it.name() })
    }

    @Test
    fun `explicit origins reject others`() {
        val source = CorsConfiguration("https://app.jaamesokhan.ir, http://localhost:*").corsConfigurationSource()
        val config = assertNotNull(source.getCorsConfiguration(requestTo("/api/v1/dictionary/meaning")))
        assertEquals("https://app.jaamesokhan.ir", config.checkOrigin("https://app.jaamesokhan.ir"))
        assertEquals("http://localhost:5173", config.checkOrigin("http://localhost:5173"))
        assertNull(config.checkOrigin("https://evil.example"))
    }

    @Test
    fun `non api paths have no cors configuration`() {
        val source = CorsConfiguration("*").corsConfigurationSource()
        assertNull(source.getCorsConfiguration(requestTo("/actuator/health")))
    }
}
