package etf.ri.rma.newsfeedapp.model

import androidx.room.Ignore
import coil.request.Tags
import etf.ri.rma.newsfeedapp.data.network.Data
import java.time.format.DateTimeFormatter
import java.util.Locale

/*
data class NewsItem(
    val uuid: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    var category: String,
    val isFeatured: Boolean,
    val source: String,
    val publishedDate: String,
    val imageTags: ArrayList<String> = arrayListOf()
){
    companion object {
        fun fromData(data: Data, isFeatured: Boolean = false): NewsItem {
            val firstCategory = if (data.categories.isNotEmpty()) data.categories[0] else "general"

            return NewsItem(
                uuid = data.uuid,
                title = data.title,
                snippet = data.description,
                imageUrl = data.image_url,
                category = firstCategory,
                isFeatured = isFeatured,
                source = data.source,
                publishedDate = data.published_at,
                imageTags = ArrayList()
            )
        }
    }

}

 */

data class NewsItem(
    val uuid: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val category: String,
    val isFeatured: Boolean,
    val source: String,
    val publishedDate: String,
    @Ignore val imageTags: List<String> = emptyList()
) {
    companion object {
        fun fromData(data: Data, isFeatured: Boolean = false): NewsItem {
            val firstCategory = if (data.categories.isNotEmpty()) data.categories[0] else "general"

            return NewsItem(
                uuid = data.uuid,
                title = data.title,
                snippet = data.description,
                imageUrl = data.image_url,
                category = firstCategory,
                isFeatured = isFeatured,
                source = data.source,
                publishedDate = data.published_at,
                imageTags = emptyList()
            )
        }
    }
}

fun NewsWithTags.toDomain(): NewsItem =
    NewsItem(
        uuid          = news.uuid,
        title         = news.title,
        snippet       = news.snippet,
        imageUrl      = null,                       // ili news.imageUrl ako ga čuvaš
        category      = news.category,
        isFeatured    = false,                      // postavi po svom pravilu
        source        = "",                         // popuni ako imaš source u entitetu
        publishedDate = news.publishedDate,
        imageTags     = tags.map { it.value }
    )


