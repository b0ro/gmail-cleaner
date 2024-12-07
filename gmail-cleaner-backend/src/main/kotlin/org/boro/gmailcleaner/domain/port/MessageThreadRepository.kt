package org.boro.gmailcleaner.domain.port

import org.boro.gmailcleaner.domain.model.AccessToken
import org.boro.gmailcleaner.domain.model.ListParams
import org.boro.gmailcleaner.domain.model.ListResult
import org.boro.gmailcleaner.domain.model.MessageThread
import org.boro.gmailcleaner.domain.model.Query

interface MessageThreadRepository {
    fun findThreads(
        params: ListParams,
        accessToken: AccessToken,
    ): ListResult<MessageThread>

    fun deleteThreads(
        query: Query,
        accessToken: AccessToken,
    ): Int
}
