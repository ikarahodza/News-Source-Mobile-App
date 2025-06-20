package etf.ri.rma.newsfeedapp.data.network

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "News")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var uuid: String,
    var title: String,
    var content: String,
    var category: String,
    var date: String,
    var imageUrl: String?,
    var source: String,
    val isFeatured: Boolean = false
)

@Entity(tableName = "Tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "value") var value: String
)

@Entity(
    tableName = "NewsTags",
    primaryKeys = ["newsId", "tagId"],
    foreignKeys = [
        ForeignKey(entity = NewsEntity::class, parentColumns = ["id"], childColumns = ["newsId"]),
        ForeignKey(entity = TagEntity::class, parentColumns = ["id"], childColumns = ["tagId"])
    ],
    indices = [Index("newsId"), Index("tagId")]
) data class NewsTagsCrossRef(
    var newsId: Int,
    var tagId: Int
)


data class NewsWithTags(
    @Embedded val news: NewsEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NewsTagsCrossRef::class,
            parentColumn = "newsId",
            entityColumn = "tagId"
        )
    )
    var tags: List<TagEntity>
)


