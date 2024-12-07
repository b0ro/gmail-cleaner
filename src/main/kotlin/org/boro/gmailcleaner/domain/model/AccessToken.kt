package org.boro.gmailcleaner.domain.model

@JvmInline
value class AccessToken(val value: String) {
    init {
        if (value.isBlank()) {
            throw InvalidAccessTokenException("Access token cannot be blank")
        }
    }
}

class InvalidAccessTokenException(message: String) : IllegalArgumentException(message)
