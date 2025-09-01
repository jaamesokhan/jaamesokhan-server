package ir.jaamebaade.base

import org.springframework.http.ResponseEntity
import java.io.Serializable

object BaseResultFactory {

    fun ok(result: ir.jaamebaade.base.BaseResult?): ResponseEntity<ir.jaamebaade.base.BaseResult> {
        return ResponseEntity.ok().body(result)
    }

    fun ok(result: Serializable?): ResponseEntity<ir.jaamebaade.base.BaseResult> {
        return ResponseEntity.ok().body(BaseResult(result ?: ""))
    }

    fun badRequest(result: BaseResult?): ResponseEntity<BaseResult>{
        return ResponseEntity.badRequest().body(result)
    }

    fun badRequest(result: String): ResponseEntity<BaseResult> {
        return ResponseEntity.badRequest().body(BaseResult(result = result))
    }

}