package org.boro.gmailcleaner.domain.port

import org.boro.gmailcleaner.domain.model.AccessToken
import org.boro.gmailcleaner.domain.model.Quota

fun interface QuotaRepository {
    fun getQuota(accessToken: AccessToken): Quota
}
