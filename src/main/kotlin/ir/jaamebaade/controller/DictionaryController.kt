package ir.jaamebaade.controller

import ir.jaamebaade.base.BaseResult
import ir.jaamebaade.base.BaseResultFactory
import ir.jaamebaade.request.WordMeaningRequest
import ir.jaamebaade.service.DictionaryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/dictionary")
@RestController
open class DictionaryController(private val dictionaryService: DictionaryService) {
    @PostMapping(value = ["/meaning"], consumes = ["application/json"], produces = ["application/json"])
    fun getWordMeaning(@RequestBody wordMeaningRequest: WordMeaningRequest): ResponseEntity<BaseResult> {
        val result =  dictionaryService.getWordMeaning(wordMeaningRequest)
        return BaseResultFactory.ok(result)
    }

}