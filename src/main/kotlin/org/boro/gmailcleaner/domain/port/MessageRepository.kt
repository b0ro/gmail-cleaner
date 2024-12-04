package org.boro.gmailcleaner.domain.port

import org.boro.gmailcleaner.domain.model.ListParams
import org.boro.gmailcleaner.domain.model.ListResult
import org.boro.gmailcleaner.domain.model.Message

interface MessageRepository {
    fun findMessages(
        params: ListParams,
        accessToken: String,
    ): ListResult<Message>

    fun deleteMessages(
        query: String,
        accessToken: String,
    ): Int
}
