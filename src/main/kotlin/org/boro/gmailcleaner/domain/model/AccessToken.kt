package org.boro.gmailcleaner.domain.model

@JvmInline
value class AccessToken(val value: String) {
    init {
        require(value.isNotBlank()) { "AccessToken cannot be blank" }
    }
}
