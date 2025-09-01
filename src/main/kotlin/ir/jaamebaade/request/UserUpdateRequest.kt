package ir.jaamebaade.request

import java.io.Serializable


class UserUpdateRequest(
    val id: Long? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val downloadedPoets: List<Int>? = null,
) : Serializable