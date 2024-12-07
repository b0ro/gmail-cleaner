package org.boro.gmailcleaner.adapter.gmail

import com.google.api.services.gmail.Gmail.Users.Messages
import com.google.api.services.gmail.model.BatchDeleteMessagesRequest
import com.google.api.services.gmail.model.ListMessagesResponse
import mu.KotlinLogging.logger
import org.boro.gmailcleaner.domain.model.AccessToken
import org.boro.gmailcleaner.domain.model.ListParams
import org.boro.gmailcleaner.domain.model.ListResult
import org.boro.gmailcleaner.domain.model.Message
import org.boro.gmailcleaner.domain.model.Query
import org.boro.gmailcleaner.domain.port.MessageRepository
import com.google.api.services.gmail.model.Message as GmailMessage

private const val MAX_IDS_CHUNK_SIZE = 1000

class GmailApiMessageRepository : GmailApiRepository, MessageRepository {
    private val logger = logger {}

    override fun findMessages(
        params: ListParams,
        accessToken: AccessToken,
    ): ListResult<Message> {
        val messages = api(accessToken).users().messages()
        val list = messages.listByQuery(params)
        val result =
            ListResult(
                elements = list?.messages?.map { message(it.id, messages) } ?: emptyList(),
                nextPageToken = list?.nextPageToken,
            )
        logger.info { "Fetching messages for [${params.query.value}]. Found ${result.elements.size} messages" }
        logger.debug { "Per page: ${params.perPage}, pageToken: ${params.pageToken}" }

        return result
    }

    override fun deleteMessages(
        query: Query,
        accessToken: AccessToken,
    ): Int {
        val messages = api(accessToken).users().messages()
        val ids = messages.listAllIds(query)

        if (ids.isEmpty()) {
            logger.info { "No messages found for [${query.value}]. Skipping" }
            return 0
        }
        logger.info { "Deleting messages for [${query.value}]. Found ${ids.size} messages" }

        ids.chunked(MAX_IDS_CHUNK_SIZE) {
            logger.debug { "Processing batch of ${it.size} ids..." }
            val request = BatchDeleteMessagesRequest().apply { this.ids = it }
            messages.batchDelete(DEFAULT_USER, request).execute()
        }
        logger.info { "Done" }

        return ids.size
    }

    private fun message(
        id: String,
        messages: Messages,
    ): Message {
        val message =
            messages.get(DEFAULT_USER, id)
                .setFormat("full")
                .execute()

        val headers = message.payload.headers.associateBy { it.name.lowercase() }
        val subject = headers["subject"]?.value ?: "No Subject"
        val from = headers["from"]?.value ?: "Unknown Sender"
        val sentDate = headers["date"]?.value ?: "Unknown Sent Date"
        val content = content(message)

        return Message(
            id = id,
            subject = subject,
            from = from,
            sentDate = sentDate,
            content = content,
        )
    }

    private fun content(message: GmailMessage): String {
        val parts = message.payload.parts ?: return ""
        for (part in parts) {
            if (part.mimeType == "text/plain" || part.mimeType == "text/html") {
                return String(part.body.decodeData() ?: ByteArray(0))
            }
        }

        return ""
    }

    private fun Messages.listAllIds(query: Query): List<String> {
        val result = mutableListOf<String>()
        var iteration = 0
        var pageToken: String? = null

        do {
            val response =
                list(DEFAULT_USER)
                    .setQ(query.value)
                    .setMaxResults(MAX_RESULT)
                    .setPageToken(pageToken)
                    .execute()

            pageToken = response.nextPageToken
            response.messages
                ?.map { it.id }
                ?.let { result += it }

            iteration++
            val found = response?.messages?.size ?: 0
            logger.debug { "Fetching ids, iteration: $iteration pageToken: $pageToken, found $found ids..." }
        } while (pageToken != null && iteration < MAX_ITERATIONS)

        logger.debug { "Ids: $result" }

        return result
    }

    private fun Messages.listByQuery(params: ListParams): ListMessagesResponse? =
        list(DEFAULT_USER)
            .setQ(params.query.value)
            .setMaxResults(params.perPage)
            .setPageToken(params.pageToken)
            .execute()
}
