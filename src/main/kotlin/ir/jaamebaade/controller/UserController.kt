package ir.jaamebaade.controller

import ir.jaamebaade.base.BaseResult
import ir.jaamebaade.base.BaseResultFactory
import ir.jaamebaade.request.UserUpdateRequest
import ir.jaamebaade.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController(private val userService: UserService) {
    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody userUpdateRequest: UserUpdateRequest,
    ): ResponseEntity<BaseResult> {
        userService.updateUser(id, userUpdateRequest)
        return BaseResultFactory.ok("updated successFully")
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<BaseResult> {
        val userDto = userService.getUser(id)
        return BaseResultFactory.ok(userDto)
    }

}