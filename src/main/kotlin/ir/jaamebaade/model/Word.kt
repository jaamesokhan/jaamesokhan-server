package ir.jaamebaade.model

import ir.jaamebaade.dto.WordDto
import jakarta.persistence.*

@Entity
@Table(name = "word")
class Word {
    @Id
    var id: Int? = null

    @ManyToOne(optional = false)
    var dictionary: Dictionary? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(columnDefinition = "TEXT", nullable = true)
    var meaning: String? = null

    fun toDto() =
        WordDto(
            name = name!!,
            meaning = meaning!!
        )
}
