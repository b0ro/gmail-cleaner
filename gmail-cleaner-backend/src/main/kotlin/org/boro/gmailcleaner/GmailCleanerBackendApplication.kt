package org.boro.gmailcleaner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GmailCleanerBackendApplication

fun main(args: Array<String>) {
    runApplication<GmailCleanerBackendApplication>(*args)
}
