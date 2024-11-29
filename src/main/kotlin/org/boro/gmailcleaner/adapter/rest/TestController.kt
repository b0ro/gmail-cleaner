package org.boro.gmailcleaner.adapter.rest

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.TEXT_PLAIN_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/hello")
class HelloBoundary {
    @GetMapping(produces = [TEXT_PLAIN_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun sayHello(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello World")
    }
}