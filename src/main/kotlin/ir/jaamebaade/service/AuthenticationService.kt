package ir.jaamebaade.service

import ir.jaamebaade.model.User
import ir.jaamebaade.repository.UserRepository
import ir.jaamebaade.request.LoginUserRequest
import ir.jaamebaade.request.RegisterUserRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService
) {
    fun register(registerUserRequest: RegisterUserRequest): Long? {
        val existingUser: Optional<User> = userRepository.findByUsername(registerUserRequest.username)
        if (existingUser.isPresent) {
            return null
        }
        val user = User(
            username = registerUserRequest.username,
            password = passwordEncoder.encode(registerUserRequest.password),
            email = registerUserRequest.email,
            firstName = registerUserRequest.firstName,
            lastName = registerUserRequest.lastName,
        )
        userRepository.save(user)
        return user.id

    }
    fun login(loginUserRequest: LoginUserRequest): String? {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(loginUserRequest.username, loginUserRequest.password))
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(loginUserRequest.username)
        val jwt = jwtService.generateToken(userDetails)
        return jwt
    }

}