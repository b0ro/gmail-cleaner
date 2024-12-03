package org.boro.gmailcleaner.domain.dto

data class ListResult<T>(val elements: List<T>, val nextPageToken: String?)
