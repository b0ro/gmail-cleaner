package org.boro.gmailcleaner.infrastructure

import org.boro.gmailcleaner.domain.CleanerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CleanerConfiguration {
    @Bean
    fun cleanerService() = CleanerService()
}
