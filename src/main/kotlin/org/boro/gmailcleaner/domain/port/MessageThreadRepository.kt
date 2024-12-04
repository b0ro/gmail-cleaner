package org.boro.gmailcleaner.domain.port

import org.boro.gmailcleaner.domain.model.ListParams
import org.boro.gmailcleaner.domain.model.ListResult
import org.boro.gmailcleaner.domain.model.MessageThread

interface MessageThreadRepository {
    fun findThreads(
        params: ListParams,
        accessToken: String,
    ): ListResult<MessageThread>

    fun deleteThreads(
        query: String,
        accessToken: String,
    ): Int
}
