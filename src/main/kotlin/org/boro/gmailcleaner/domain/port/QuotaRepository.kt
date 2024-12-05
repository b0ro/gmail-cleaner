package org.boro.gmailcleaner.domain.port

import org.boro.gmailcleaner.domain.model.AccessToken

interface QuotaRepository {
    fun getQuotaBytesTotal(accessToken: AccessToken): String

    fun getQuotaBytesUsed(accessToken: AccessToken): String

    fun getQuotaBytesUsedInTrash(accessToken: AccessToken): String
}
