package etf.ri.rma.newsfeedapp.data.network.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiService {
    @GET("v1/news/top/")
    suspend fun getTopStoriesByCategory(
        @Query("categories") category: String? = null,
        @Query("limit") limit: Int = 3,
        @Query("api_token") apiToken: String? = null,
        @Query("keywords") keywords: String? = null,
        @Query("language") language: String = "en",
        @Query("locale") locale: String = "us",
        @Query("relevance_score") relevance_score: Int? = null,
        @Query("snippet") snippet: Boolean = true
    ): NewsApiResponse

    @GET("v1/news/similar/{uuid}")
    suspend fun getSimilarStories(
        @Path("uuid") uuid: String,
        @Query("limit") limit: Int = 2,
        @Query("api_token") apiToken: String? = null
    ): NewsApiResponse


}





