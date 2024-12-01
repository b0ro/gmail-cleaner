package org.boro.gmailcleaner.domain

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.BatchDeleteMessagesRequest
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import mu.KotlinLogging.logger
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

    fun findMessagesBySender(
        sender: String,
        accessToken: String,
    ): List<Message> {
        val messages = api(accessToken).users().messages()
        val result =
            messages.listBySender(sender)
                .map { message(it.id, messages) }

        logger.info { "Fetching messages sent by: $sender. Found ${result.size} messages" }

        return result
    }

    fun deleteMessagesBySender(
        sender: String,
        accessToken: String,
    ): Int {
        val messages = api(accessToken).users().messages()
        val ids = messages.listAllIdsBySender(sender)

        if (ids.isEmpty()) {
            logger.info { "No messages found for $sender. Skipping" }
            return 0
        }
        logger.info { "Deleting messages from: $sender. Found ${ids.size} messages" }

        ids.chunked(MAX_IDS_CHUNK_SIZE) {
            logger.debug { "Processing batch of ${it.size} ids" }

            val request = BatchDeleteMessagesRequest().apply { this.ids = it }
            messages.batchDelete(DEFAULT_USER, request).execute()
        }
        logger.info { "Done" }

        return ids.size
    }

    fun findThreadsBySender(
        sender: String,
        accessToken: String,
    ): List<MessageThread> {
        val threads = api(accessToken).users().threads()
        val result =
            threads.listBySender(sender)
                .map { thread(it) }

        logger.info { "Fetching threads sent by: $sender. Found ${result.size} threads" }

        return result
    }

    fun deleteThreadsBySender(
        sender: String,
        accessToken: String,
    ): Int {
        val threads = api(accessToken).users().threads()
        val ids = threads.listAllIdsBySender(sender)

        if (ids.isEmpty()) {
            logger.info { "No threads found for $sender. Skipping" }
            return 0
        }
        logger.info { "Deleting threads from: $sender. Found ${ids.size} threads" }

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

    private fun GmailMessages.listAllIdsBySender(sender: String): List<String> {
        val result = mutableListOf<String>()
        var iteration = 0
        var pageToken: String? = null

        do {
            val response =
                list(DEFAULT_USER)
                    .setQ("from:$sender")
                    .setMaxResults(MAX_RESULT)
                    .apply {
                        if (pageToken != null) {
                            setPageToken(pageToken)
                        }
                    }
                    .execute()

            pageToken = response.nextPageToken
            response.messages
                ?.map { it.id }
                ?.let { result += it }

            iteration++
            val found = response?.messages?.size
            logger.debug { "Fetching ids, iteration: $iteration pageToken: $pageToken, found $found ids" }
        } while (pageToken != null && iteration < MAX_ITERATIONS)

        logger.debug { "Ids: $result" }

        return result
    }

    private fun GmailThreads.listAllIdsBySender(sender: String): List<String> {
        val result = mutableListOf<String>()
        var iteration = 0
        var pageToken: String? = null

        do {
            val response =
                list(DEFAULT_USER)
                    .setQ("from:$sender")
                    .setMaxResults(MAX_RESULT)
                    .apply {
                        if (pageToken != null) {
                            setPageToken(pageToken)
                        }
                    }
                    .execute()

            pageToken = response.nextPageToken
            response.threads
                ?.map { it.id }
                ?.let { result += it }

            iteration++
            val found = response?.threads?.size
            logger.debug { "Fetching ids, iteration: $iteration pageToken: $pageToken, found $found ids" }
        } while (pageToken != null && iteration < MAX_ITERATIONS)

        logger.debug { "Ids: $result" }

        return result
    }

    private fun GmailMessages.listBySender(sender: String): List<GmailMessage> =
        list(DEFAULT_USER)
            .setQ("from:$sender")
            .execute()
            .messages ?: emptyList()

    private fun GmailThreads.listBySender(sender: String): List<GmailThread> =
        list(DEFAULT_USER)
            .setQ("from:$sender")
            .execute()
            .threads ?: emptyList()

    companion object {
        val JSON_FACTORY: GsonFactory = GsonFactory.getDefaultInstance()
    }
}
