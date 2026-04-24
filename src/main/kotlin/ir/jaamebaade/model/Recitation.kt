package ir.jaamebaade.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
@Table(name = "recitations")
class Recitation(
    @Id
    @GeneratedValue
    @UuidGenerator
    var id: UUID? = null,

    @Column(nullable = false, unique = true)
    var audioId: Int? = null,

    @Column(nullable = false)
    var artistName: String? = null,

    @Column(nullable = false)
    var poemId: Int? = null,

    @Column(nullable = true)
    var audioFileUrl: String? = null,

    @Column(nullable = true)
    var syncFileUrl: String? = null,
)
