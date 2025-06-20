package etf.ri.rma.newsfeedapp.data.network

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.toDomain
import etf.ri.rma.newsfeedapp.model.toEntity

@Dao
interface SavedNewsDAO {
    @Transaction
    @Query("SELECT * FROM News")
    suspend fun loadAllNews(): List<NewsEntity>

    @Transaction
    @Query("SELECT * FROM News WHERE category = :category")
    suspend fun newsWithCategory(category: String): List<NewsEntity>

    suspend fun getNewsWithCategory(category: String): List<NewsItem> =
        newsWithCategory(category).map { it.toDomain() }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: NewsEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewsTagCrossRef(ref: NewsTagsCrossRef): Long

    @Query("SELECT * FROM Tags WHERE value IN (:tags)")
    suspend fun getExistingTags(tags: List<String>): List<TagEntity>

    @Query("SELECT id FROM Tags WHERE value = :value LIMIT 1")
    suspend fun findTagId(value: String): Int?

    @Transaction
    @Query("""
        SELECT DISTINCT News.* FROM News 
        INNER JOIN NewsTags ON News.id = NewsTags.newsId 
        INNER JOIN Tags ON Tags.id = NewsTags.tagId 
        WHERE Tags.value IN (:tags) 
        ORDER BY date DESC
    """)
    suspend fun getSimilarNewsRaw(tags: List<String>): List<NewsWithTags>

    @Transaction
    @Query("""
        SELECT Tags.value FROM Tags 
        INNER JOIN NewsTags ON Tags.id = NewsTags.tagId 
        WHERE NewsTags.newsId = :newsId
    """)
    suspend fun getTags(newsId: Int): List<String>

    @Query("SELECT * FROM News WHERE uuid = :newsId")
    suspend fun getNewsById(newsId: String): NewsEntity?

    @Transaction
    @Query("SELECT * FROM News WHERE id = :newsId")
    fun loadWithTags(newsId: Int): NewsWithTags

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
        return insertNews(news.toEntity()) != -1L
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
                val inserted = insertTag(TagEntity(value = tag))
                if (inserted == -1L) {
                    // Tag je postojao, dohvatimo pravi ID
                    findTagId(tag)!!.toLong()
                } else {
                    newTagCount++
                    inserted
                }
            }
            insertNewsTagCrossRef(NewsTagsCrossRef(newsId, tagId.toInt()))
        }
        return newTagCount
    }

    suspend fun allNews(): List<NewsItem> =
        loadAllNews().map { it.toDomain() }

    /*
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



fun NewsEntity.toDomain(): NewsItem {
    return NewsItem(
        uuid = uuid,
        title = title,
        snippet = content,
        category = category,
        source = source,
        publishedDate = date,
        imageUrl = imageUrl,
        isFeatured = isFeatured,
        imageTags = emptyList()
    )
}
    fun toNewsItem(entity: NewsEntity, tags: List<String>) = NewsItem(
        uuid          = entity.uuid,
        title         = entity.title,
        snippet       = entity.content,
        source        = entity.source,
        publishedDate = entity.date,
        imageUrl      = entity.imageUrl,
        category      = entity.category,
        isFeatured    = entity.isFeatured,
        imageTags     = ArrayList(tags)
    )



