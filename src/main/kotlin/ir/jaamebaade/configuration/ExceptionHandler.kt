package ir.jaamebaade.configuration

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
open class ExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(RuntimeException::class)
    fun handleRunTimeException(ex: RuntimeException, request: WebRequest): ResponseEntity<Any> {
        logger.error("Unexpected Exception", ex)
        return handleExceptionInternal(ex, null, HttpHeaders(), INTERNAL_SERVER_ERROR, request)!!
    }
}