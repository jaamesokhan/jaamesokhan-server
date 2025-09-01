package ir.jaamebaade.request

import java.io.Serializable

class RegisterUserRequest (
    val username: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val password: String,
) : Serializable