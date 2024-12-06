package org.boro.gmailcleaner.adapter.drive

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.boro.gmailcleaner.domain.model.AccessToken
import org.boro.gmailcleaner.domain.model.Quota
import org.boro.gmailcleaner.domain.port.QuotaRepository
import java.math.RoundingMode.HALF_UP
import com.google.auth.oauth2.AccessToken as GmailAccessToken

private const val APPLICATION_NAME = "GmailCleaner"
private val JSON_FACTORY: GsonFactory = GsonFactory.getDefaultInstance()

class GoogleDriveApiQuotaRepository : QuotaRepository {
    override fun getQuota(accessToken: AccessToken): Quota {
        val result = api(accessToken)
            .about()
            .get()
            .setFields("user, storageQuota")
            .execute()
            .storageQuota

        return Quota(
            total = result.limit.bytesToGigaBytes(),
            used = result.usage.bytesToGigaBytes(),
            usedInTrash = result.usageInDriveTrash.bytesToGigaBytes(),
        )
    }

    private fun api(token: AccessToken): Drive =
        Drive.Builder(
            newTrustedTransport(),
            JSON_FACTORY,
            HttpCredentialsAdapter(
                GoogleCredentials.create(
                    GmailAccessToken(token.value, null),
                ),
            ),
        ).setApplicationName(APPLICATION_NAME)
            .build()

    private fun Long.bytesToGigaBytes(scale: Int = 2): Float =
        this.toFloat()
            .div(1024 * 1024 * 1024)
            .toBigDecimal()
            .setScale(scale, HALF_UP)
            .toFloat()
}