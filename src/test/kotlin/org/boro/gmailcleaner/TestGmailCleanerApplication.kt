package org.boro.gmailcleaner

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<GmailCleanerApplication>().with(TestcontainersConfiguration::class).run(*args)
}
