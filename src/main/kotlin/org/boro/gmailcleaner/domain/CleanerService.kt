package org.boro.gmailcleaner.domain

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.BatchDeleteMessagesRequest
import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.api.services.gmail.model.ListThreadsResponse
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import mu.KotlinLogging.logger
import org.boro.gmailcleaner.domain.dto.ListResult
import org.boro.gmailcleaner.domain.dto.Message
import org.boro.gmailcleaner.domain.dto.MessageThread
import com.google.api.services.gmail.Gmail.Users.Messages as GmailMessages
import com.google.api.services.gmail.Gmail.Users.Threads as GmailThreads
import com.google.api.services.gmail.model.Message as GmailMessage
import com.google.api.services.gmail.model.Thread as GmailThread

private const val MAX_ITERATIONS = 100
private const val APPLICATION_NAME = "GmailCleaner"
private const val MAX_RESULT = 1000L
private const val MAX_IDS_CHUNK_SIZE = 1000
private const val DEFAULT_USER = "me"

class CleanerService {
    private val logger = logger {}

    fun findMessages(
        params: ListParams,
        accessToken: String
    ): ListResult<Message> {
        val messages = api(accessToken).users().messages()
        val list = messages.listByQuery(params)

        val result = ListResult(
            elements = list?.messages?.map { message(it.id, messages) } ?: emptyList(),
            nextPageToken = list?.nextPageToken,
        )

        logger.debug { "Per page: ${params.perPage}, pageToken: ${params.pageToken}" }
        logger.info { "Fetching messages for [${params.query}]. Found ${result.elements.size} messages" }

        return result
    }

    fun deleteMessages(
        query: String,
        accessToken: String,
    ): Int {
        val messages = api(accessToken).users().messages()
        val ids = messages.listAllIds(query)

        if (ids.isEmpty()) {
            logger.info { "No messages found for [$query]. Skipping" }
            return 0
        }
        logger.info { "Deleting messages for [$query]. Found ${ids.size} messages" }

        ids.chunked(MAX_IDS_CHUNK_SIZE) {
            logger.debug { "Processing batch of ${it.size} ids" }

            val request = BatchDeleteMessagesRequest().apply { this.ids = it }
            messages.batchDelete(DEFAULT_USER, request).execute()
        }
        logger.info { "Done" }

        return ids.size
    }

    fun findThreads(
        params: ListParams,
        accessToken: String,
    ): ListResult<MessageThread> {
        val threads = api(accessToken).users().threads()
        val list =
            threads.listByQuery(params)

        val result = ListResult(
            elements = list?.threads?.map { thread(it) } ?: emptyList(),
            nextPageToken = list?.nextPageToken,
        )

        logger.debug { "Per page: ${params.perPage}, pageToken: ${params.pageToken}" }
        logger.info { "Fetching threads for [${params.query}]. Found ${result.elements.size} threads" }

        return result
    }

    fun deleteThreads(
        query: String,
        accessToken: String,
    ): Int {
        val threads = api(accessToken).users().threads()
        val ids = threads.listAllIds(query)

        if (ids.isEmpty()) {
            logger.info { "No threads found for [$query]. Skipping" }
            return 0
        }
        logger.info { "Deleting threads for [$query]. Found ${ids.size} threads" }

        ids.forEach { threads.delete(DEFAULT_USER, it).execute() }

        logger.info { "Done" }

        return ids.size
    }

    private fun message(
        id: String,
        messages: GmailMessages,
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

    private fun thread(thread: GmailThread): MessageThread =
        MessageThread(
            id = thread.id,
            historyId = thread.historyId.toLong(),
            preview = thread.snippet.trimEnd(),
        )

    private fun content(message: GmailMessage): String {
        val parts = message.payload.parts ?: return ""
        for (part in parts) {
            if (part.mimeType == "text/plain" || part.mimeType == "text/html") {
                return String(part.body.decodeData() ?: ByteArray(0))
            }
        }
        return ""
    }

    private fun api(token: String): Gmail {
        return Gmail.Builder(
            newTrustedTransport(),
            JSON_FACTORY,
            HttpCredentialsAdapter(
                GoogleCredentials.create(AccessToken(token, null)),
            ),
        ).setApplicationName(APPLICATION_NAME)
            .build()
    }

    private fun GmailMessages.listAllIds(query: String): List<String> {
        val result = mutableListOf<String>()
        var iteration = 0
        var pageToken: String? = null

        do {
            val response =
                list(DEFAULT_USER)
                    .setQ(query)
                    .setMaxResults(MAX_RESULT)
                    .setPageToken(pageToken)
                    .execute()

            pageToken = response.nextPageToken
            response.messages
                ?.map { it.id }
                ?.let { result += it }

            iteration++
            val found = response?.messages?.size ?: 0
            logger.debug { "Fetching ids, iteration: $iteration pageToken: $pageToken, found $found ids" }
        } while (pageToken != null && iteration < MAX_ITERATIONS)

        logger.debug { "Ids: $result" }

        return result
    }

    private fun GmailThreads.listAllIds(query: String): List<String> {
        val result = mutableListOf<String>()
        var iteration = 0
        var pageToken: String? = null

        do {
            val response =
                list(DEFAULT_USER)
                    .setQ(query)
                    .setMaxResults(MAX_RESULT)
                    .setPageToken(pageToken)
                    .execute()

            pageToken = response.nextPageToken
            response.threads
                ?.map { it.id }
                ?.let { result += it }

            iteration++
            val found = response?.threads?.size ?: 0
            logger.debug { "Fetching ids, iteration: $iteration pageToken: $pageToken, found $found ids" }
        } while (pageToken != null && iteration < MAX_ITERATIONS)

        logger.debug { "Ids: $result" }

        return result
    }

    private fun GmailMessages.listByQuery(params: ListParams): ListMessagesResponse? =
        list(DEFAULT_USER)
            .setQ(params.query)
            .setMaxResults(params.perPage)
            .setPageToken(params.pageToken)
            .execute()

    private fun GmailThreads.listByQuery(params: ListParams): ListThreadsResponse? =
        list(DEFAULT_USER)
            .setQ(params.query)
            .setMaxResults(params.perPage)
            .setPageToken(params.pageToken)
            .execute()

    companion object {
        val JSON_FACTORY: GsonFactory = GsonFactory.getDefaultInstance()
    }
}

data class ListParams(val query: String, val perPage: Long, val pageToken: String?)