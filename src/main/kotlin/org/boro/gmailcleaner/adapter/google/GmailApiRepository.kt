package org.boro.gmailcleaner.adapter.google

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials

const val MAX_ITERATIONS = 100
const val MAX_RESULT = 1000L
const val DEFAULT_USER = "me"

private const val APPLICATION_NAME = "GmailCleaner"
private val JSON_FACTORY: GsonFactory = GsonFactory.getDefaultInstance()

interface GmailApiRepository {
    fun api(token: String): Gmail {
        return Gmail.Builder(
            newTrustedTransport(),
            JSON_FACTORY,
            HttpCredentialsAdapter(
                GoogleCredentials.create(
                    AccessToken(token, null),
                ),
            ),
        ).setApplicationName(APPLICATION_NAME)
            .build()
    }
}
