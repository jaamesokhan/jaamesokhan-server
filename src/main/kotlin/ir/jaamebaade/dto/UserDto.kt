package ir.jaamebaade.dto

import java.io.Serializable

class UserDto(
    val id: Long,
    val firstName: String?,
    val lastName: String?,
    val username: String,
    val email: String?,
    val downloadedPoets: List<PoetDto>?,
) : Serializable