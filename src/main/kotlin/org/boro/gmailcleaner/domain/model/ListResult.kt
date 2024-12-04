package org.boro.gmailcleaner.domain.model

data class ListResult<T>(val elements: List<T>, val nextPageToken: String?)
