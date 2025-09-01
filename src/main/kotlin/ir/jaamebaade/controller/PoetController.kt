package ir.jaamebaade.controller

import ir.jaamebaade.dto.PoetDto
import ir.jaamebaade.service.PoetService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/poet")
class PoetController(private val poetService: PoetService) {
    @GetMapping(value = [""], produces = ["application/json"])
    fun list(
        @PageableDefault(page = 0, size = 10) pageable: Pageable,
        @RequestParam(required = false) name: String? = null,
    ): ResponseEntity<Page<PoetDto>> {
        val poetDtoList = poetService.listPoets(pageable, name)
        return ResponseEntity.ok(poetDtoList)
    }

    @GetMapping(value = ["/download/{id}"])
    fun download(@PathVariable id: Int): ResponseEntity<HttpHeaders> {
        val downloadUrl = poetService.downloadPoet(id)
        val headers = HttpHeaders()
        headers.location = URI(downloadUrl)
        return ResponseEntity(headers, HttpStatus.FOUND)
    }
}