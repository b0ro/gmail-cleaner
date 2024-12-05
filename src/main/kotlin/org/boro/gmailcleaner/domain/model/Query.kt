package org.boro.gmailcleaner.domain.model

@JvmInline
value class Query(val value: String) {
    init {
        require(value.isNotBlank()) { "Query cannot be blank" }
    }
}
