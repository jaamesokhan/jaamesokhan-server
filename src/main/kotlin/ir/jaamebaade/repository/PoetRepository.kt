package ir.jaamebaade.repository

import ir.jaamebaade.model.Poet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface PoetRepository : PagingAndSortingRepository<Poet, Int> {
    fun findById(id: Int): Poet

    fun findByNameContainsOrderById(name: String, pageable: Pageable): Page<Poet>

    fun findAllByOrderById(pageable: Pageable): Page<Poet>
}