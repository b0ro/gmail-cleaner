package org.boro.gmailcleaner.domain

data class Message(
    val id: String,
    val subject: String,
    val from: String,
    val sentDate: String,
    val content: String,
)
