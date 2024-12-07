package org.boro.gmailcleaner.infrastructure

import org.boro.gmailcleaner.adapter.drive.GoogleDriveApiQuotaRepository
import org.boro.gmailcleaner.adapter.gmail.GmailApiMessageRepository
import org.boro.gmailcleaner.adapter.gmail.GmailApiMessageThreadRepository
import org.boro.gmailcleaner.domain.CleanerFacade
import org.boro.gmailcleaner.domain.port.MessageRepository
import org.boro.gmailcleaner.domain.port.MessageThreadRepository
import org.boro.gmailcleaner.domain.port.QuotaRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CleanerConfiguration {
    @Bean
    fun messageRepository(): MessageRepository = GmailApiMessageRepository()

    @Bean
    fun messageThreadRepository(): MessageThreadRepository = GmailApiMessageThreadRepository()

    @Bean
    fun quotaRepository(): QuotaRepository = GoogleDriveApiQuotaRepository()

    @Bean
    fun cleanerService(
        messageRepository: MessageRepository,
        messageThreadRepository: MessageThreadRepository,
        quotaRepository: QuotaRepository,
    ): CleanerFacade =
        CleanerFacade(
            messageRepository = messageRepository,
            messageThreadRepository = messageThreadRepository,
            quotaRepository = quotaRepository,
        )
}
