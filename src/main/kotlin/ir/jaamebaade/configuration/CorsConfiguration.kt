package ir.jaamebaade.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.cors.CorsConfiguration as SpringCorsConfiguration

/**
 * Lets browser clients (the PWA) call the public API from another origin.
 * Allowed origins come from `cors.allowed-origins` (comma-separated, patterns allowed).
 */
@Configuration
open class CorsConfiguration(
    @Value("\${cors.allowed-origins:*}") private val allowedOriginsProperty: String,
) {

    @Bean
    open fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = SpringCorsConfiguration().apply {
            allowedOriginPatterns = allowedOriginsProperty.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            allowedMethods = listOf("GET", "POST", "OPTIONS")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Location")
            maxAge = 3600
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", configuration)
        }
    }
}
