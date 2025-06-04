package etf.ri.rma.newsfeedapp.data.network.api

data class ImagaApiResponse(
    val result: TagResult,
    val status: Status
)

data class TagResult(
    val tags: List<Tag>
)

data class Tag(
    val confidence: Double,
    val tag: TagInfo
)

data class TagInfo(
    val en: String
)

data class Status(
    val text: String,
    val type: String
)
