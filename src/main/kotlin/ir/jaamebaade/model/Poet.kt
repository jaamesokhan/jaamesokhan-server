package ir.jaamebaade.model

import ir.jaamebaade.dto.PoetDto
import jakarta.persistence.*

@Entity
@Table(name = "poets")
class Poet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(columnDefinition = "TEXT", nullable = true)
    var description: String? = null

    @Column(nullable = true)
    var imageUrl: String? = null

    fun toDto() =
        PoetDto(
            id = id!!,
            name = name!!,
            description = description,
            imageUrl = imageUrl
        )
}