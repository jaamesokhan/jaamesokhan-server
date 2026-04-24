package ir.jaamebaade.dto

import java.io.Serializable

@Suppress("unused")
class RecitationDto(
    val audioId: Int,
    val artistName: String,
    val poemId: Int,
    val audioFileUrl: String?,
    val syncFileUrl: String?,
) : Serializable
