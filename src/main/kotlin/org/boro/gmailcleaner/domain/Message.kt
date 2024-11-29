package org.boro.gmailcleaner.domain

data class Message(
    val from: List<String>,
    val subject: String,
    val content: String,
    val sentDate: String
)