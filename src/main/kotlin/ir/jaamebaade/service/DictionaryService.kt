package ir.jaamebaade.service

import ir.jaamebaade.dto.WordDto
import ir.jaamebaade.repository.WordRepository
import ir.jaamebaade.request.WordMeaningRequest
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class DictionaryService(
    private val wordRepository: WordRepository,
) {
    @Cacheable(value = ["wordMeaning"], key = "#wordMeaningRequest.word")
    fun getWordMeaning(wordMeaningRequest: WordMeaningRequest): WordDto? {
        val words = wordRepository.findAllByName(normalizeWord(wordMeaningRequest.word))
        if (words.isEmpty())
            return WordDto(
                name = wordMeaningRequest.word,
                meaning = ""
            )

        val wordDto = WordDto(
            name = words.first().name!!,
            meaning = words.map { it.meaning }.joinToString { it + '\n' }.trim()
        )
        return wordDto
    }

    private fun normalizeWord(word: String): String {
        return word.replace(Regex("[\\u064B-\\u0652]"), "")
    }
}