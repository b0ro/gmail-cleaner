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
        rootUrl: String,
    ): ListResult<MessageThread>

    fun deleteThreads(
        query: Query,
        accessToken: AccessToken,
        rootUrl: String,
    ): Int
}
