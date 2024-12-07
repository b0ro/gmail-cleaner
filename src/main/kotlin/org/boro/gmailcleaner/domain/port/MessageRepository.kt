package org.boro.gmailcleaner.domain.port

import org.boro.gmailcleaner.domain.model.AccessToken
import org.boro.gmailcleaner.domain.model.ListParams
import org.boro.gmailcleaner.domain.model.ListResult
import org.boro.gmailcleaner.domain.model.Message
import org.boro.gmailcleaner.domain.model.Query

interface MessageRepository {
    fun findMessages(
        params: ListParams,
        accessToken: AccessToken,
    ): ListResult<Message>

    fun deleteMessages(
        query: Query,
        accessToken: AccessToken,
    ): Int
}
