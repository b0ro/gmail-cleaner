package org.boro.gmailcleaner.domain.model

data class ListParams(
    val query: Query,
    val perPage: Long,
    val pageToken: String?,
)
