package ir.jaamebaade.model

import jakarta.persistence.*

@Entity
@Table(name = "categories")
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @ManyToOne(optional = false)
    var poet: Poet? = null

    @Column(nullable = false)
    var text: String? = null

    @Column(nullable = false)
    var parentId: Int? = null
}