package ir.jaamebaade.repository

import ir.jaamebaade.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : CrudRepository<User, Long>  {
    fun findByUsername(username: String): Optional<User>
}