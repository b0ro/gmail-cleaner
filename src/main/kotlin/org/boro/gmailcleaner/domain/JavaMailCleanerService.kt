package org.boro.gmailcleaner.domain

import jakarta.mail.Folder
import jakarta.mail.Message as JavaMailMessage
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.search.SearchTerm
import mu.KotlinLogging
import java.util.Properties

class JavaMailCleanerService() : CleanerService {
    private val logger = KotlinLogging.logger {}

    fun findMessagesBySender(sender: String, credentials: Credentials): List<Message> {
        logger.info { "Sender is: $sender" }

        val session = session()
        try {
            val batchSize = 100
            var start = 1
            var end = batchSize

            // connect to the messages store
            val store = session.store
            store.connect(credentials.username, credentials.password)

            // list all folders
            val folders = store.defaultFolder.list().map { it.name }.joinToString(", ")
            logger.info { "Available folders: $folders" }

            // open inbox
            val inbox = store.getFolder("INBOX")
            inbox.open(Folder.READ_ONLY)

            logger.info { "Started searching..." }
            val searchTerm = bySender(sender)

            val found = mutableListOf<Message>()
            do {
                val messages = inbox.getMessages(start, end)
                logger.info { "Found messages: ${messages.toMessages()}" }

                val searchResult = inbox.search(searchTerm, messages)
                found += searchResult.toMessages()
                logger.info { "Processed batch between $start and $end. Found ${searchResult.size} matches." }

                start += batchSize
                end += batchSize

            } while (end < 1000)

            logger.info { "Found messages ${found.size}" }

            inbox.close()
            store.close()

            return found.toList()
        } catch (exception: Exception) {
            throw CleanerServiceException.couldNotFindMessages(exception)
        }
    }

    private fun session(): Session {
        val properties = Properties()

        // server setting
        properties["mail.store.protocol"] = "imap"
        properties["mail.imap.host"] = "imap.gmail.com"
        properties["mail.imap.port"] = "993"

        // SSL setting
        properties["mail.imap.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        properties["mail.imap.socketFactory.fallback"] = "false"
        properties["mail.imap.socketFactory.port"] = "993"

        return Session.getDefaultInstance(properties)
    }

    private fun bySender(sender: String): SearchTerm {
        return object: SearchTerm() {
            override fun match(message: JavaMailMessage): Boolean {
                return message.from.asList().contains(InternetAddress(sender))
            }
        }
    }

    private fun Array<JavaMailMessage>.toMessages(): List<Message> {
        return map { Message(
            from = it.from.map { from -> from.toString() },
            subject = it.subject,
            content = it.content.toString(),
            sentDate = it.sentDate.toString()
        ) }
    }
}