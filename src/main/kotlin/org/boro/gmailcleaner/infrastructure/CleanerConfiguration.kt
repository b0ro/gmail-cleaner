package org.boro.gmailcleaner.infrastructure

import org.boro.gmailcleaner.adapter.google.GmailApiMessageRepository
import org.boro.gmailcleaner.adapter.google.GmailApiMessageThreadRepository
import org.boro.gmailcleaner.domain.CleanerFacade
import org.boro.gmailcleaner.domain.port.MessageRepository
import org.boro.gmailcleaner.domain.port.MessageThreadRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CleanerConfiguration {
    @Bean
    fun messageRepository(): MessageRepository = GmailApiMessageRepository()

    @Bean
    fun messageThreadRepository(): MessageThreadRepository = GmailApiMessageThreadRepository()

    @Bean
    fun cleanerService(
        messageRepository: MessageRepository,
        messageThreadRepository: MessageThreadRepository,
    ): CleanerFacade = CleanerFacade(messageRepository, messageThreadRepository)
}
