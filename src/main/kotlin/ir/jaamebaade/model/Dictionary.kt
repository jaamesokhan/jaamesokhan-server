package ir.jaamebaade.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "dictionary")
class Dictionary {
    @Id
    var id: Int? = null

    @Column(nullable = false)
    var engName: String? = null

    @Column(nullable = false)
    var faName: String? = null
}