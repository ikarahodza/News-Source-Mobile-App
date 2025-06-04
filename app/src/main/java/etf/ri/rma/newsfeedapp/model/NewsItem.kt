package etf.ri.rma.newsfeedapp.model

import etf.ri.rma.newsfeedapp.data.network.Data
import java.time.format.DateTimeFormatter
import java.util.Locale

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
