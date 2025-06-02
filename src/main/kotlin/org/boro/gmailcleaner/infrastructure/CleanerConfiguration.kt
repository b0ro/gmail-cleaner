package org.boro.gmailcleaner.infrastructure

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.boro.gmailcleaner.adapter.drive.GoogleDriveApiQuotaRepository
import org.boro.gmailcleaner.adapter.gmail.GmailApiMessageRepository
import org.boro.gmailcleaner.adapter.gmail.GmailApiMessageThreadRepository
import org.boro.gmailcleaner.domain.CleanerFacade
import org.boro.gmailcleaner.domain.port.MessageRepository
import org.boro.gmailcleaner.domain.port.MessageThreadRepository
import org.boro.gmailcleaner.domain.port.QuotaRepository
import org.boro.gmailcleaner.infrastructure.CleanerConfiguration.GoogleApisProperties
import org.hibernate.validator.constraints.URL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@EnableConfigurationProperties(GoogleApisProperties::class)
class CleanerConfiguration(
    val apisProperties: GoogleApisProperties,
) {
    @Bean
    fun messageRepository(): MessageRepository = GmailApiMessageRepository()

    @Bean
    fun messageThreadRepository(): MessageThreadRepository = GmailApiMessageThreadRepository()

    @Bean
    fun quotaRepository(): QuotaRepository = GoogleDriveApiQuotaRepository()

    @Bean
    fun cleanerFacade(
        messageRepository: MessageRepository,
        messageThreadRepository: MessageThreadRepository,
        quotaRepository: QuotaRepository,
    ): CleanerFacade =
        CleanerFacade(
            messageRepository = messageRepository,
            messageThreadRepository = messageThreadRepository,
            quotaRepository = quotaRepository,
            gmailRootUrl = apisProperties.gmail.rootUrl,
            driveRootUrl = apisProperties.drive.rootUrl,
        )

    @Validated
    @ConfigurationProperties(prefix = "googleapis")
    data class GoogleApisProperties(
        @field:Valid val gmail: Gmail,
        @field:Valid val drive: Drive,
    ) {
        data class Gmail(
            @field:Valid
            @field:NotBlank
            @field:URL
            val rootUrl: String,
        )

        data class Drive(
            @field:Valid
            @field:NotBlank
            @field:URL
            val rootUrl: String,
        )
    }
}
