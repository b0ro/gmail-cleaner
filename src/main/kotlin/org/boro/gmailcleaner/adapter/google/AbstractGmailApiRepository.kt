package org.boro.gmailcleaner.adapter.google

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials

abstract class AbstractGmailApiRepository {
    protected fun api(token: String): Gmail {
        return Gmail.Builder(
            newTrustedTransport(),
            JSON_FACTORY,
            HttpCredentialsAdapter(
                GoogleCredentials.create(AccessToken(token, null)),
            ),
        ).setApplicationName(APPLICATION_NAME)
            .build()
    }

    companion object {
        @JvmStatic
        protected val MAX_ITERATIONS = 100

        @JvmStatic
        protected val APPLICATION_NAME = "GmailCleaner"

        @JvmStatic
        protected val MAX_RESULT = 1000L

        @JvmStatic
        protected val MAX_IDS_CHUNK_SIZE = 1000

        @JvmStatic
        protected val DEFAULT_USER = "me"

        private val JSON_FACTORY: GsonFactory = GsonFactory.getDefaultInstance()
    }
}
