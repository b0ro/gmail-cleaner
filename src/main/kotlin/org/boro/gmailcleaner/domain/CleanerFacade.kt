package org.boro.gmailcleaner.domain

import org.boro.gmailcleaner.domain.model.AccessToken
import org.boro.gmailcleaner.domain.model.ListParams
import org.boro.gmailcleaner.domain.model.ListResult
import org.boro.gmailcleaner.domain.model.Message
import org.boro.gmailcleaner.domain.model.MessageThread
import org.boro.gmailcleaner.domain.model.Query
import org.boro.gmailcleaner.domain.model.Quota
import org.boro.gmailcleaner.domain.port.MessageRepository
import org.boro.gmailcleaner.domain.port.MessageThreadRepository
import org.boro.gmailcleaner.domain.port.QuotaRepository

class CleanerFacade(
    private val messageRepository: MessageRepository,
    private val messageThreadRepository: MessageThreadRepository,
    private val quotaRepository: QuotaRepository,
) {
    fun findMessages(
        listParams: ListParams,
        accessToken: AccessToken,
    ): ListResult<Message> = messageRepository.findMessages(listParams, accessToken)

    fun deleteMessages(
        query: Query,
        accessToken: AccessToken,
    ): Int = messageRepository.deleteMessages(query, accessToken)

    fun findThreads(
        listParams: ListParams,
        accessToken: AccessToken,
    ): ListResult<MessageThread> = messageThreadRepository.findThreads(listParams, accessToken)

    fun deleteThreads(
        query: Query,
        accessToken: AccessToken,
    ): Int = messageThreadRepository.deleteThreads(query, accessToken)

    fun getQuota(accessToken: AccessToken): Quota = quotaRepository.getQuota(accessToken)
}
