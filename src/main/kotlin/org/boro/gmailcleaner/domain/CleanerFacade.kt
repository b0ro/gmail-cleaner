package org.boro.gmailcleaner.domain

import org.boro.gmailcleaner.domain.model.ListParams
import org.boro.gmailcleaner.domain.model.ListResult
import org.boro.gmailcleaner.domain.model.Message
import org.boro.gmailcleaner.domain.model.MessageThread
import org.boro.gmailcleaner.domain.port.MessageRepository
import org.boro.gmailcleaner.domain.port.MessageThreadRepository

class CleanerFacade(
    private val messageRepository: MessageRepository,
    private val threadRepository: MessageThreadRepository,
) {
    fun findMessages(
        listParams: ListParams,
        accessToken: String,
    ): ListResult<Message> = messageRepository.findMessages(listParams, accessToken)

    fun deleteMessages(
        query: String,
        accessToken: String,
    ): Int = messageRepository.deleteMessages(query, accessToken)

    fun findThreads(
        listParams: ListParams,
        accessToken: String,
    ): ListResult<MessageThread> = threadRepository.findThreads(listParams, accessToken)

    fun deleteThreads(
        query: String,
        accessToken: String,
    ): Int = threadRepository.deleteThreads(query, accessToken)
}
