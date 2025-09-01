package ir.jaamebaade.model

import jakarta.persistence.*

@Entity
@Table(name = "verses")
class Verse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @ManyToOne(optional = false)
    var poem: Poem? = null

    /**
     * This field indicates the order of the verse in the poem.
     */
    @Column(nullable = false)
    var verseOrder: Int? = null

    /**
     * This field indicates the position of the verse in one line. (can be 0 or 1)
     * TODO Could be an Enum
     */
    @Column(nullable = false)
    var position: Int? = null

    @Column(columnDefinition = "TEXT", nullable = false)
    var text: String? = null
}