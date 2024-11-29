package org.boro.gmailcleaner.adapter.rest

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.boro.gmailcleaner.domain.JavaMailCleanerService
import org.boro.gmailcleaner.domain.Credentials
import org.boro.gmailcleaner.domain.Message
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("cleaner")
class CleanerController(val service: JavaMailCleanerService) {

    @PostMapping("/messages")
    fun findMessages(
        @RequestParam sender: String,
        @RequestBody @Valid credentials: CredentialsRequest
    ): List<Message> = service.findMessagesBySender(sender, credentials.toDomain())
}

data class CredentialsRequest(
    @field:NotBlank(message = "Username cannot be blank")
    val username: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String
)

private fun CredentialsRequest.toDomain() = Credentials(
    username = username,
    password = password,
)