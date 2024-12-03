package org.boro.gmailcleaner.adapter.rest

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import org.boro.gmailcleaner.domain.CleanerService
import org.boro.gmailcleaner.domain.ListParams
import org.boro.gmailcleaner.domain.dto.ListResult
import org.boro.gmailcleaner.domain.dto.Message
import org.boro.gmailcleaner.domain.dto.MessageThread
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private const val BLANK_QUERY_PARAM_MESSAGE = "Query parameter cannot be blank"
private const val MAX_PER_PAGE_PARAM_MESSAGE = "There can be maximum 500 elements per page"
private const val ACCESS_TOKEN_HEADER = "AccessToken"

@RestController
@RequestMapping("cleaner")
class CleanerController(val service: CleanerService) {
    @GetMapping("/messages")
    fun findMessages(
        @RequestParam
        @NotBlank(message = BLANK_QUERY_PARAM_MESSAGE)
        query: String,
        @RequestParam(required = false, defaultValue = "100")
        @Max(message = MAX_PER_PAGE_PARAM_MESSAGE, value = 500)
        perPage: Long,
        @RequestParam(required = false)
        pageToken: String?,
        @RequestHeader(ACCESS_TOKEN_HEADER) accessToken: String,
    ): ListResponse<Message> =
        service.findMessages(
            ListParams(
                query = query,
                perPage = perPage,
                pageToken = pageToken,
            ),
            accessToken = accessToken,
        ).toListResponse()

    @DeleteMapping("/messages")
    fun deleteMessages(
        @RequestParam
        @NotBlank(message = BLANK_QUERY_PARAM_MESSAGE)
        query: String,
        @RequestHeader(ACCESS_TOKEN_HEADER) accessToken: String,
    ): DeletedResponse =
        service.deleteMessages(query, accessToken)
            .toDeletedResponse()

    @GetMapping("/threads")
    fun findThreads(
        @RequestParam
        @NotBlank(message = BLANK_QUERY_PARAM_MESSAGE)
        query: String,
        @RequestParam(required = false, defaultValue = "100")
        @Max(message = MAX_PER_PAGE_PARAM_MESSAGE, value = 500)
        perPage: Long,
        @RequestParam(required = false)
        pageToken: String?,
        @RequestHeader(ACCESS_TOKEN_HEADER) accessToken: String,
    ): ListResponse<MessageThread> =
        service.findThreads(
            ListParams(
                query = query,
                perPage = perPage,
                pageToken = pageToken,
            ),
            accessToken = accessToken,
        ).toListResponse()

    @DeleteMapping("/threads")
    fun deleteThreads(
        @RequestParam
        @NotBlank(message = BLANK_QUERY_PARAM_MESSAGE)
        query: String,
        @RequestHeader(ACCESS_TOKEN_HEADER) accessToken: String,
    ): DeletedResponse =
        service.deleteThreads(query, accessToken)
            .toDeletedResponse()
}

data class ListResponse<T>(val count: Int, val elements: List<T>, val nextPageToken: String?)

data class DeletedResponse(val deletedElements: Int)

private fun <T> ListResult<T>.toListResponse(): ListResponse<T> =
    ListResponse(
        count = this.elements.size,
        elements = this.elements,
        nextPageToken = this.nextPageToken,
    )

private fun Int.toDeletedResponse(): DeletedResponse =
    DeletedResponse(
        deletedElements = this,
    )
