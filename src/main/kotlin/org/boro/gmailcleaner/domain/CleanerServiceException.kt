package org.boro.gmailcleaner.domain

class CleanerServiceException(message: String, cause: Exception) : Exception(message, cause) {
    companion object {
        fun couldNotFindMessages(cause: Exception): CleanerServiceException =
            CleanerServiceException("Could not find messages", cause)
    }
}
