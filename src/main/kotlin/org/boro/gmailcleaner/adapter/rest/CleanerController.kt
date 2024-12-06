package org.boro.gmailcleaner.adapter.rest

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import org.boro.gmailcleaner.domain.CleanerFacade
import org.boro.gmailcleaner.domain.model.AccessToken
import org.boro.gmailcleaner.domain.model.ListParams
import org.boro.gmailcleaner.domain.model.ListResult
import org.boro.gmailcleaner.domain.model.Message
import org.boro.gmailcleaner.domain.model.MessageThread
import org.boro.gmailcleaner.domain.model.Query
import org.boro.gmailcleaner.domain.model.Quota
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
@RequestMapping("api")
class CleanerController(private val facade: CleanerFacade) {
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
        @RequestHeader(ACCESS_TOKEN_HEADER)
        accessToken: String,
    ): ListResponse<Message> =
        facade.findMessages(
            ListParams(
                query = Query(query),
                perPage = perPage,
                pageToken = pageToken,
            ),
            accessToken = AccessToken(accessToken),
        ).toListResponse()

    @DeleteMapping("/messages")
    fun deleteMessages(
        @RequestParam
        @NotBlank(message = BLANK_QUERY_PARAM_MESSAGE)
        query: String,
        @RequestHeader(ACCESS_TOKEN_HEADER)
        accessToken: String,
    ): DeletedResponse =
        facade.deleteMessages(
            Query(query),
            AccessToken(accessToken),
        ).toDeletedResponse()

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
        @RequestHeader(ACCESS_TOKEN_HEADER)
        accessToken: String,
    ): ListResponse<MessageThread> =
        facade.findThreads(
            ListParams(
                query = Query(query),
                perPage = perPage,
                pageToken = pageToken,
            ),
            accessToken = AccessToken(accessToken),
        ).toListResponse()

    @DeleteMapping("/threads")
    fun deleteThreads(
        @RequestParam
        @NotBlank(message = BLANK_QUERY_PARAM_MESSAGE)
        query: String,
        @RequestHeader(ACCESS_TOKEN_HEADER)
        accessToken: String,
    ): DeletedResponse =
        facade.deleteThreads(
            Query(query),
            AccessToken(accessToken),
        ).toDeletedResponse()

    @GetMapping("/quota")
    fun getQuota(
        @RequestHeader(ACCESS_TOKEN_HEADER) accessToken: String,
    ): Quota = facade.getQuota(AccessToken(accessToken))
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
