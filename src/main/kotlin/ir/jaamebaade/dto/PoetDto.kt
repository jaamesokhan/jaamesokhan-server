package ir.jaamebaade.dto

import java.io.Serializable


class PoetDto(
    val id: Int,
    val name: String,
    val description: String?,
    val imageUrl: String?,
): Serializable