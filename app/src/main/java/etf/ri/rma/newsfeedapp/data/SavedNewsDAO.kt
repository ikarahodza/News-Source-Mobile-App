package etf.ri.rma.newsfeedapp.data

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import etf.ri.rma.newsfeedapp.data.NewsService.mapUIToAPICategory
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.News
import etf.ri.rma.newsfeedapp.model.NewsTags
import etf.ri.rma.newsfeedapp.model.NewsWithTags
import etf.ri.rma.newsfeedapp.model.Tags


@Dao
interface SavedNewsDAO {

    @Query("SELECT * FROM News")
    suspend fun loadAllNews(): List<News>

    @Transaction
    @Query("SELECT * FROM News")
    suspend fun loadAllNewsWithTags(): List<NewsWithTags>

    @Transaction
    @Query("SELECT * FROM News WHERE category = :category")
    suspend fun newsWithCategory(category: String): List<News>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: News): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: Tags): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewsTagCrossRef(ref: NewsTags): Long

    @Query("SELECT * FROM Tags WHERE value IN (:tags)")
    suspend fun getExistingTags(tags: List<String>): List<Tags>

    @Query("SELECT id FROM Tags WHERE value = :value LIMIT 1")
    suspend fun findTagId(value: String): Int?


    @Transaction
    @Query("""
        SELECT DISTINCT News.* FROM News 
        INNER JOIN NewsTags ON News.id = NewsTags.newsId 
        INNER JOIN Tags ON Tags.id = NewsTags.tagsId 
        WHERE Tags.value IN (:tags) 
        ORDER BY publishedDate DESC
    """)
    suspend fun getSimilarNewsRaw(tags: List<String>): List<NewsWithTags>


    @Transaction
    @Query("""
        SELECT Tags.value FROM Tags 
        INNER JOIN NewsTags ON Tags.id = NewsTags.tagsId 
        WHERE NewsTags.newsId = :newsId
    """)
    suspend fun getTags(newsId: Int): List<String>

    @Query("SELECT * FROM News WHERE uuid = :newsId")
    suspend fun getNewsById(newsId: String): News?

    @Transaction
    @Query("SELECT * FROM News WHERE id = :newsId")
    fun loadWithTags(newsId: Int): NewsWithTags

    @Transaction
    @Query("SELECT Tags.value FROM Tags")
    suspend fun getAllTags() : List<String>

    suspend fun saveNews(news: NewsItem): Boolean {
        if (getNewsById(news.uuid) != null) return false
        val mappedNews = news.toEntity()
        Log.d("SAVE_NEWS", "Mapped category to: ${mappedNews.category}")
        return insertNews(mappedNews) != -1L
    }


    @Query("SELECT id FROM News WHERE uuid = :uuid LIMIT 1")
    suspend fun getNewsIdByUuid(uuid: String): Int?

    @Transaction
    suspend fun addTags(tags: List<String>, newsId: Int): Int {
        var newTagCount = 0
        val existing = getExistingTags(tags).associateBy { it.value }

        for (tag in tags) {
            val tagId: Long = existing[tag]?.id?.toLong() ?: run {
                val inserted = insertTag(Tags(value = tag))
                if (inserted == -1L) {
                    findTagId(tag)!!.toLong()
                } else {
                    newTagCount++
                    inserted
                }
            }
            insertNewsTagCrossRef(NewsTags(
                newsId = newsId,
                tagsId = tagId.toInt()))
        }
        return newTagCount
    }

    @Transaction
    @Query("""
        SELECT Tags.* FROM Tags 
        INNER JOIN NewsTags ON Tags.id = NewsTags.tagsId 
        WHERE NewsTags.newsId = :newsId
    """)
    suspend fun getTagsFull(newsId: Int): List<Tags>

    suspend fun allNews(): List<NewsItem> =
        loadAllNewsWithTags().map { newsWithTags ->
            toNewsItem(newsWithTags.news, newsWithTags.tags)
        }

    suspend fun getSimilarNews(tags: List<String>): List<NewsItem> {
        val firstTwo = tags.take(2)
        return getSimilarNewsRaw(firstTwo).map { rel ->
            toNewsItem(rel.news, rel.tags)
        }
    }

    suspend fun getNewsWithCategory(category: String): List<NewsItem> {
        // Convert UI category to API category before querying
        val apiCategory = mapUIToAPICategory(category)
        return newsWithCategory(apiCategory).map { news ->
            val tags = getTagsFull(news.id)
            toNewsItem(news, tags)
        }
    }

}


/* ---------------------------------------------------------------- */

fun NewsItem.toEntity() = News(
    uuid = uuid,
    title = title,
    snippet = snippet,
    category = mapUIToAPICategory(category),
    publishedDate = publishedDate,
    imageUrl = imageUrl,
    source = source,
    isFeatured = isFeatured
)


fun News.toDomain(): NewsItem {
    return NewsItem(
        uuid = uuid,
        title = title,
        snippet = snippet,
        category = NewsService.mapAPIToUICategory(category),
        source = source,
        publishedDate = publishedDate,
        imageUrl = imageUrl,
        isFeatured = isFeatured,
        imageTags = emptyList()
    )
}
fun toNewsItem(entity: News, tags: List<Tags>) = NewsItem(
    uuid          = entity.uuid,
    title         = entity.title,
    snippet       = entity.snippet,
    source        = entity.source,
    publishedDate = entity.publishedDate,
    imageUrl      = entity.imageUrl,
    category      = NewsService.mapAPIToUICategory(entity.category),
    isFeatured    = entity.isFeatured,
    imageTags     = tags
)



