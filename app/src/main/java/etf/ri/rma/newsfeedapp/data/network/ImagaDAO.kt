package etf.ri.rma.newsfeedapp.data.network

import android.util.Log
import etf.ri.rma.newsfeedapp.data.network.retrofit.RetrofitInstanceImaga
import etf.ri.rma.newsfeedapp.data.network.api.*
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImagaDAO {

    private var apiService: ImagaApiService = RetrofitInstanceImaga.api

    private val imageTagsCache = mutableMapOf<String, ImagaApiResponse>()

    private val emptyResponse = ImagaApiResponse(
        result = TagResult(tags = emptyList()),
        status = Status(text = "No tags found", type = "error")
    )

    fun setApiService(service: ImagaApiService) {
        this.apiService = service
    }

    suspend fun getTags(imageUrl: String) = getImageTags(imageUrl)

    suspend fun getImageTags(imageUrl: String): ImagaApiResponse = withContext(Dispatchers.IO) {
        if (!isValidImageUrl(imageUrl)) {
            throw InvalidImageURLException("Invalid image URL: $imageUrl")
        }

        if (imageTagsCache.containsKey(imageUrl)) {
            return@withContext imageTagsCache[imageUrl]!!
        }

        try {
            val response = apiService.getTags(imageUrl)
            imageTagsCache[imageUrl] = response
            return@withContext response
        } catch (e: Exception) {
            Log.e("ImagaDAO", "Greška pri dohvatu tagova", e)
            return@withContext emptyResponse
        }
    }

    private fun isValidImageUrl(url: String): Boolean {
        if (url.isNullOrEmpty()) {
            return false
        }

        try {
            return (url.startsWith("http://") || url.startsWith("https://"))
        } catch (e: Exception) {
            return false
        }
    }

}