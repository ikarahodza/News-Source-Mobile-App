package etf.ri.rma.newsfeedapp.data

import android.util.Log
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object NewsService {
    private val newsDAO = NewsData.getNewsDAO()
    private val imagaDAO = ImagaDAO()

    private val _allNews = MutableStateFlow<List<NewsItem>>(emptyList())
    val allNews: StateFlow<List<NewsItem>> = _allNews.asStateFlow()

    private var initialDataLoaded = false
    private val categoryTimestamps = mutableMapOf<String, Long>()
    private val imageTagsCache = mutableMapOf<String, ArrayList<String>>()
    private val similarStoriesCache = mutableMapOf<String, List<NewsItem>>()
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            loadInitialData()
        }
    }

    private suspend fun loadInitialData() {
        try {
            val initialData = newsDAO.getAllStories()
            _allNews.value = initialData.map { NewsItem.fromData(it) }
            Log.d("NewsService", "Initial data loaded with ${_allNews.value.size} items")
        } catch (e: Exception) {
            Log.e("NewsService", "Error loading initial data", e)
        }
    }

    suspend fun getAllNews(): List<NewsItem> {
        if (_allNews.value.isEmpty()) {
            loadInitialData()
        }
        return _allNews.value
    }


    suspend fun getNewsByCategory(category: String, forceRefresh: Boolean = false): List<NewsItem> {
        if (category == "Sve") {
            return getAllNews()
        }

        val apiCategory = mapUIToAPICategory(category)
        val currentTime = System.currentTimeMillis()
        val lastFetchTime = categoryTimestamps[category] ?: 0L
        val needsRefresh = forceRefresh || (currentTime - lastFetchTime) >= 30000

        Log.d("NewsService", "needsRefresh for category $category: $needsRefresh")

        try {
            if (needsRefresh) {
                val topStories = newsDAO.getTopStoriesByCategory(apiCategory)

                val featuredNewsItems = topStories.map { storyData ->
                    val newsItem = NewsItem.fromData(storyData, true)
                    newsItem.copy(category = category)
                }

                val currentNews = _allNews.value.map { it.copy(isFeatured = false) }.toMutableList()

                for (featuredItem in featuredNewsItems) {
                    val existingIndex = currentNews.indexOfFirst { it.uuid == featuredItem.uuid }
                    if (existingIndex < 0) {
                        currentNews.add(0, featuredItem)
                    } else {
                        currentNews[existingIndex] = featuredItem
                    }
                }

                _allNews.value = currentNews
                categoryTimestamps[category] = currentTime
            }

            return _allNews.value.filter { newsItem ->
                if (category == "Nauka/tehnologija") {
                    newsItem.category.equals("Nauka/tehnologija", ignoreCase = true) ||
                            newsItem.category.equals("science", ignoreCase = true) ||
                            newsItem.category.equals("tech", ignoreCase = true)
                } else {
                    newsItem.category.equals(category, ignoreCase = true)
                }
            }.sortedByDescending { it.isFeatured }

        } catch (e: Exception) {
            Log.e("NewsService", "Error fetching news for category $category: ${e.message}", e)
            return filterByCategory(_allNews.value, category)
        }
    }

    private fun filterByCategory(news: List<NewsItem>, category: String): List<NewsItem> {
        return news.filter { newsItem ->
            when (category) {
                "Nauka/tehnologija" ->
                    newsItem.category.equals("Nauka/tehnologija", ignoreCase = true) ||
                            newsItem.category.equals("science", ignoreCase = true) ||
                            newsItem.category.equals("tech", ignoreCase = true)
                "Sport" ->
                    newsItem.category.equals("Sport", ignoreCase = true) ||
                            newsItem.category.equals("sports", ignoreCase = true)
                "Politika" ->
                    newsItem.category.equals("Politika", ignoreCase = true) ||
                            newsItem.category.equals("politics", ignoreCase = true)
                "Zdravlje" ->
                    newsItem.category.equals("Zdravlje", ignoreCase = true) ||
                            newsItem.category.equals("health", ignoreCase = true)
                else ->
                    newsItem.category.equals(category, ignoreCase = true)
            }
        }.sortedByDescending { it.isFeatured }
    }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> {
        if (similarStoriesCache.containsKey(uuid)) {
            return similarStoriesCache[uuid]!!
        }

        try {
            val similarData = newsDAO.getSimilarStories(uuid)
            val similarItems = similarData.map {
                NewsItem.fromData(it).copy(isFeatured = true)
            }


            if (similarItems.isNotEmpty()) {
                similarStoriesCache[uuid] = similarItems
            }

            val currentNews = _allNews.value.map { it.copy(isFeatured = false) }.toMutableList()
            val newItems = similarItems.filter { item -> currentNews.none { it.uuid == item.uuid } }
            if (newItems.isNotEmpty()) {
                currentNews.addAll(0, newItems)
                _allNews.value = currentNews
            }

            return similarItems
        } catch (e: Exception) {

            if (similarStoriesCache[uuid]?.isNotEmpty() == true) {
                return similarStoriesCache[uuid]!!
            }

            val story = _allNews.value.find { it.uuid == uuid }
            return if (story != null) {
                _allNews.value.filter {
                    it.uuid != uuid && it.category.any { cat ->
                        story.category.contains(cat)
                    }
                }.take(2)
            } else {
                emptyList()
            }
        }
    }

    suspend fun getImageTags(imageUrl: String?): ArrayList<String> {
        if (imageUrl == null || imageUrl.isBlank()) {
            return ArrayList()
        }

        if (imageTagsCache.containsKey(imageUrl)) {
            return imageTagsCache[imageUrl]!!
        }

        return try {
            val tagsResponse = imagaDAO.getImageTags(imageUrl)
            val tags = ArrayList<String>()

            tagsResponse.result?.tags?.forEach { tagItem ->
                tagItem.tag?.en?.let {
                    tags.add(it)
                }
            }

            imageTagsCache[imageUrl] = tags
            tags
        } catch (e: Exception) {
            Log.e("NewsService", "Error fetching image tags: ${e.message}", e)
            ArrayList()
        }
    }

    fun getNewsById(uuid: String): NewsItem? {
        return _allNews.value.find { it.uuid == uuid }
    }

    public fun mapUIToAPICategory(category: String): String {
        Log.d("CATEGORY_MAP", "Mapping '$category'")
        return when (category) {
            "Politika" -> "politics"
            "Sport" -> "sports"
            "Nauka" -> "science"
            "Tehnologija" -> "tech"
            "Nauka/tehnologija" -> "tech"
            "Zdravlje" -> "health"
            "Zabava" -> "entertainment"
            "Biznis" -> "business"
            "Hrana" -> "food"
            "Putovanja" -> "travel"
            "None", "Sve", "All" -> "general"
            else -> category.lowercase()
        }
    }

    public fun mapAPIToUICategory(apiCategory: String): String {
        return when (apiCategory.lowercase()) {
            "politics" -> "Politika"
            "sports" -> "Sport"
            "tech", "science", "technology" -> "Nauka/tehnologija"
            "health" -> "Zdravlje"
            "entertainment" -> "Zabava"
            "business" -> "Biznis"
            "food" -> "Hrana"
            "travel" -> "Putovanja"
            "general" -> "Sve"
            else -> apiCategory
        }
    }
}