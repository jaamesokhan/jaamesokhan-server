package ir.jaamebaade.model

import jakarta.persistence.*

@Entity
@Table(name = "poems")
class Poem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @ManyToOne(optional = false)
    var category: Category? = null

    @Column(nullable = false)
    var title: String? = null
}