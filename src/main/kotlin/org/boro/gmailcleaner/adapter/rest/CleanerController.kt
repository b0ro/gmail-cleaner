package org.boro.gmailcleaner.adapter.rest

import org.boro.gmailcleaner.domain.CleanerService
import org.boro.gmailcleaner.domain.Message
import org.boro.gmailcleaner.domain.MessageThread
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("cleaner")
class CleanerController(val service: CleanerService) {
    @GetMapping("/messages")
    fun findMessages(
        @RequestParam sender: String,
        @RequestHeader("AccessToken") accessToken: String,
    ): MessagesResponse =
        service.findMessagesBySender(sender, accessToken)
            .toListResponse()

    @DeleteMapping("/messages")
    fun deleteMessages(
        @RequestParam sender: String,
        @RequestHeader("AccessToken") accessToken: String,
    ): DeletedMessagesResponse =
        service.deleteMessagesBySender(sender, accessToken)
            .toDeletedMessagesResponse()

    @GetMapping("/threads")
    fun findThreads(
        @RequestParam sender: String,
        @RequestHeader("AccessToken") accessToken: String,
    ): List<MessageThread> = service.findThreadsBySender(sender, accessToken)

    @DeleteMapping("/threads")
    fun deleteThreads(
        @RequestParam sender: String,
        @RequestHeader("AccessToken") accessToken: String,
    ): DeletedThreadsResponse =
        service.deleteThreadsBySender(sender, accessToken)
            .toDeletedThreadsResponse()
}

data class MessagesResponse(val count: Int, val messages: List<Message>)

data class DeletedMessagesResponse(val deletedMessages: Int)

data class DeletedThreadsResponse(val deletedThreads: Int)

private fun Int.toDeletedMessagesResponse(): DeletedMessagesResponse =
    DeletedMessagesResponse(
        deletedMessages = this,
    )

private fun Int.toDeletedThreadsResponse(): DeletedThreadsResponse =
    DeletedThreadsResponse(
        deletedThreads = this,
    )

private fun List<Message>.toListResponse(): MessagesResponse =
    MessagesResponse(
        count = count(),
        messages = this,
    )
