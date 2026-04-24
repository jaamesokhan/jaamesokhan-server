package ir.jaamebaade.controller

import ir.jaamebaade.dto.RecitationDto
import ir.jaamebaade.service.RecitationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/recitations")
class RecitationController(
    private val recitationService: RecitationService,
) {
    @GetMapping("/{poemId}", produces = ["application/json"])
    fun listByPoemId(@PathVariable poemId: Int): ResponseEntity<List<RecitationDto>> {
        return ResponseEntity.ok(recitationService.listByPoemId(poemId))
    }
}
