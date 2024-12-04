package org.boro.gmailcleaner.domain.model

data class ListParams(
    val query: String,
    val perPage: Long,
    val pageToken: String?,
)
