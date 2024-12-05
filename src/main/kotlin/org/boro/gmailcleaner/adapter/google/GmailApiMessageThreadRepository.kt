package org.boro.gmailcleaner.adapter.google

import com.google.api.services.gmail.Gmail.Users.Threads
import com.google.api.services.gmail.model.ListThreadsResponse
import com.google.api.services.gmail.model.Thread
import mu.KotlinLogging.logger
import org.boro.gmailcleaner.domain.model.ListParams
import org.boro.gmailcleaner.domain.model.ListResult
import org.boro.gmailcleaner.domain.model.MessageThread
import org.boro.gmailcleaner.domain.port.MessageThreadRepository

class GmailApiMessageThreadRepository : GmailApiRepository, MessageThreadRepository {
    private val logger = logger {}

    override fun findThreads(
        params: ListParams,
        accessToken: String,
    ): ListResult<MessageThread> {
        val threads = api(accessToken).users().threads()
        val list = threads.listByQuery(params)
        val result =
            ListResult(
                elements = list?.threads?.map { thread(it) } ?: emptyList(),
                nextPageToken = list?.nextPageToken,
            )
        logger.debug { "Per page: ${params.perPage}, pageToken: ${params.pageToken}" }
        logger.info { "Fetching threads for [${params.query}]. Found ${result.elements.size} threads" }

        return result
    }

    override fun deleteThreads(
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

    private fun thread(thread: Thread): MessageThread =
        MessageThread(
            id = thread.id,
            historyId = thread.historyId.toLong(),
            preview = thread.snippet.trimEnd(),
        )

    private fun Threads.listAllIds(query: String): List<String> {
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

    private fun Threads.listByQuery(params: ListParams): ListThreadsResponse? =
        list(DEFAULT_USER)
            .setQ(params.query)
            .setMaxResults(params.perPage)
            .setPageToken(params.pageToken)
            .execute()
}
