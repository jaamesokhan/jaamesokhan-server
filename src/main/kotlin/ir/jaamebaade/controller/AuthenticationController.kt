package ir.jaamebaade.controller

import ir.jaamebaade.base.BaseResultFactory
import ir.jaamebaade.request.RegisterUserRequest
import ir.jaamebaade.base.BaseResult
import ir.jaamebaade.request.LoginUserRequest
import ir.jaamebaade.service.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(
    private val authenticationService: AuthenticationService,
) {
    @PostMapping(value = ["/register"], consumes = ["application/json"], produces = ["application/json"])
    fun register(@RequestBody registerUserRequest: RegisterUserRequest): ResponseEntity<BaseResult> {
        val id: Long = authenticationService.register(registerUserRequest)
            ?: return BaseResultFactory.badRequest("User already exists")
        return BaseResultFactory.ok(id)
    }

    @PostMapping(value = ["/login"], consumes = ["application/json"], produces = ["application/json"])
    fun login(@RequestBody loginUserRequest: LoginUserRequest): ResponseEntity<BaseResult> {
        val token: String = authenticationService.login(loginUserRequest)
            ?: return BaseResultFactory.badRequest("Username and Password doesn't match")
        return BaseResultFactory.ok(token)
    }
}