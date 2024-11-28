package org.boro.gmailcleaner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GmailCleanerApplication

fun main(args: Array<String>) {
    runApplication<GmailCleanerApplication>(*args)
}
