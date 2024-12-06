package org.boro.gmailcleaner.domain.model

data class Quota(
    val total: Float,
    val used: Float,
    val usedInTrash: Float,
)
