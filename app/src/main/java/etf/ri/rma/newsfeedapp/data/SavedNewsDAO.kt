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

    suspend fun getNewsWithCategory(category: String): List<NewsItem> =
        newsWithCategory(category).map { it.toDomain() }

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

    /*
    @Transaction
    suspend fun saveNews(news: NewsItem): Boolean =
        insertNews(news.toEntity()) != -1L

    suspend fun saveNews(item: NewsItem): Boolean {
        if (loadAllNews().any { it.uuid == item.uuid }) return false
        insertNews(
            NewsEntity(
                uuid = item.uuid,
                title = item.title,
                content = item.snippet,
                source = item.source,
                date = item.publishedDate,
                imageUrl = item.imageUrl,
                category = item.category,
                isFeatured = item.isFeatured
            )
        )
        return true
    }

     */

    suspend fun saveNews(news: NewsItem): Boolean {
        if (getNewsById(news.uuid) != null) return false
        val mappedNews = news.toEntity()
        Log.d("SAVE_NEWS", "Mapped category to: ${mappedNews.category}")
        return insertNews(mappedNews) != -1L
    }


    @Query("SELECT id FROM News WHERE uuid = :uuid LIMIT 1")
    suspend fun getNewsIdByUuid(uuid: String): Int?

    /*
    @Transaction
    suspend fun addTags(tags: List<String>, newsId: Int): Int {
        var newTagCount = 0
        val existing = getExistingTags(tags).associateBy { it.value }

        for (tag in tags) {
            val tagId = existing[tag]?.id ?: run {
                val newId = insertTag(TagEntity(value = tag)).toInt()
                if (newId > 0) newTagCount++
                newId
            }
            insertNewsTagCrossRef(NewsTagsCrossRef(newsId, tagId))
        }
        return newTagCount
    }

     */

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


    suspend fun allNews(): List<NewsItem> =
        loadAllNews().map { news ->
            val tags = getTags(news.id) // Ovo mora biti implementirano
            news.toDomain().copy(imageTags = tags)
        }


    /*
    suspend fun allNews(): List<NewsItem>  {
        val newsWithTags = loadAllNewsWithTags()
        return newsWithTags.map { newsWithTag ->
            toNewsItem(newsWithTag.news, newsWithTag.tags.map { it.value })
        }
    }

    suspend fun getSimilarNews(tags: List<String>): List<NewsItem> {
        val newsList = getSimilarNewsRaw(tags)
        return newsList.map { news ->
            val tagList = getTags(news.id)
            NewsItem(
                uuid = news.uuid,
                title = news.title,
                snippet = news.content,
                category = news.category,
                source = news.source,
                publishedDate = news.date,
                imageUrl = news.imageUrl,
                isFeatured = false,
                imageTags = tagList
            )
        }
    }


     */
    suspend fun getSimilarNews(tags: List<String>): List<NewsItem> {
        val firstTwo = tags.take(2)
        return getSimilarNewsRaw(firstTwo).map { rel ->
            toNewsItem(rel.news, rel.tags.map { it.value })
        }
    }


}



/*
@Dao
interface SavedNewsDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(news: NewsEntity): Long

    @Query("SELECT * FROM News")
    fun loadAll(): List<NewsEntity>

    @Query("SELECT * FROM News WHERE category = :category")
    fun loadByCategory(category: String): List<NewsEntity>

    @Transaction
    @Query("SELECT * FROM News WHERE id = :newsId")
    fun loadWithTags(newsId: Int): NewsWithTags

    @Transaction
    @Query("""
      SELECT *
      FROM News
      INNER JOIN NewsTags ON News.id = NewsTags.newsId
      INNER JOIN Tags ON Tags.id = NewsTags.tagId
      WHERE Tags.value IN(:tags)
      ORDER BY publishedDate DESC
    """)
    fun loadSimilar(tags: List<String>): List<NewsWithTags>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(tag: TagEntity): Long

    @Query("SELECT * FROM Tags WHERE value = :value LIMIT 1")
    fun findByValue(value: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCrossRef(ref: NewsTags)

}

 */



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

private fun NewsWithTags.toDomain() = NewsItem(
    uuid          = news.uuid,
    title         = news.title,
    snippet       = news.snippet,
    category      = news.category,
    publishedDate = news.publishedDate,
    imageUrl      = news.imageUrl,
    source        = news.source,
    isFeatured    = news.isFeatured,
    imageTags     = tags.map { it.value }
)




fun News.toDomain(): NewsItem {
    return NewsItem(
        uuid = uuid,
        title = title,
        snippet = snippet,
        category = category,
        source = source,
        publishedDate = publishedDate,
        imageUrl = imageUrl,
        isFeatured = isFeatured,
        imageTags = emptyList()
    )
}
    fun toNewsItem(entity: News, tags: List<String>) = NewsItem(
        uuid          = entity.uuid,
        title         = entity.title,
        snippet       = entity.snippet,
        source        = entity.source,
        publishedDate = entity.publishedDate,
        imageUrl      = entity.imageUrl,
        category      = entity.category,
        isFeatured    = entity.isFeatured,
        imageTags     = ArrayList(tags)
    )



