package etf.ri.rma.newsfeedapp.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "News")
data class News(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "uuid") var uuid: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "snippet") var snippet: String,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "publishedDate") var publishedDate: String,
    @ColumnInfo(name = "imageUrl") var imageUrl: String?,
    @ColumnInfo(name = "source") var source: String,
    @ColumnInfo(name = "isFeatured") val isFeatured: Boolean = false
)


@Entity(tableName = "Tags")
data class Tags(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "value") var value: String
)

@Entity(
    tableName = "NewsTags",
    foreignKeys = [
        ForeignKey(entity = News::class, parentColumns = ["id"], childColumns = ["newsId"]),
        ForeignKey(entity = Tags::class, parentColumns = ["id"], childColumns = ["tagsId"])
    ],
    indices = [Index("newsId"), Index("tagsId")]
) data class NewsTags(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var newsId: Int,
    var tagsId: Int
)




data class NewsWithTags(
    @Embedded val news: News,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NewsTags::class,
            parentColumn = "newsId",
            entityColumn = "tagsId"
        )
    )
    var tags: List<Tags>
)

fun News.toDomain() = NewsItem(
    uuid          = uuid,
    title         = title,
    snippet       = snippet,
    imageUrl      = imageUrl,
    category      = category,
    isFeatured    = isFeatured,
    source        = source,
    publishedDate = publishedDate,
    imageTags     = emptyList()
)




