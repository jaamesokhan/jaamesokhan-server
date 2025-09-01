package ir.jaamebaade.configuration

import ir.jaamebaade.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import java.io.IOException

@Component
open class JwtAuthenticationFilter (
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter(){
    private val handlerExceptionResolver: HandlerExceptionResolver? = null


    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var authCookie: String? = null
        val cookies = request.cookies

        if (cookies != null) {
            for (cookie in cookies) {
                if (cookie.name == "Authorization") {
                    authCookie = cookie.value
                    break
                }
            }
        }
        val authHeader = request.getHeader("Authorization")

        if (authCookie == null && (authHeader == null || !authHeader.startsWith("API "))
        ) {
            filterChain.doFilter(request, response)
            return
        }
        try {
            val jwt = authHeader?.substring(4) ?: authCookie
            val username: String = jwtService.extractUsername(jwt)

            val authentication = SecurityContextHolder.getContext().authentication

            if (authentication == null) {
                val userDetails = userDetailsService.loadUserByUsername(username)

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )

                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }

            filterChain.doFilter(request, response)
        } catch (exception: Exception) {
            handlerExceptionResolver!!.resolveException(request, response, null, exception)
        }
    }
}