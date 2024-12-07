package org.boro.gmailcleaner.domain.model

@JvmInline
value class Query(val value: String) {
    init {
        if (value.isBlank()) {
            throw InvalidQueryException("Query cannot be blank")
        }
    }
}

class InvalidQueryException(message: String) : IllegalArgumentException(message)
