package ir.jaamebaade.model

import ir.jaamebaade.dto.UserDto
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    private val username: String? = null,

    @Column(nullable = false)
    var email: String? = null, // Should add validation on this

    @Column(nullable = false)
    private val password: String? = null,

    @Column(nullable = true)
    var firstName: String? = null,

    @Column(nullable = true)
    var lastName: String? = null,

    @OneToMany
    var downloadedPoets: List<Poet>? = null,
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return Collections.singleton(SimpleGrantedAuthority("user"))
    }

    override fun getPassword(): String {
        return password!!
    }

    override fun getUsername(): String {
        return username!!
    }


    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true

    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    fun toDto() = UserDto(
        id = id!!,
        firstName = firstName,
        lastName = lastName,
        username = username!!,
        email = email,
        downloadedPoets = downloadedPoets?.map { it.toDto() }
    )
}