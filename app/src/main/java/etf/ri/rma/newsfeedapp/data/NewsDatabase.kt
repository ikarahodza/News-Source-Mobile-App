package etf.ri.rma.newsfeedapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import etf.ri.rma.newsfeedapp.data.network.NewsEntity
import etf.ri.rma.newsfeedapp.data.network.NewsTagsCrossRef
import etf.ri.rma.newsfeedapp.data.network.SavedNewsDAO
import etf.ri.rma.newsfeedapp.data.network.TagEntity

@Database (
    entities = [NewsEntity::class, TagEntity::class, NewsTagsCrossRef::class],
    version = 3,
    exportSchema = false
)
abstract class  NewsDatabase : RoomDatabase() {
    abstract fun savedNewsDAO(): SavedNewsDAO

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getInstance(context: Context): NewsDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news-db"
                ).fallbackToDestructiveMigration()
                    .build().also {INSTANCE = it}
            }
        }

    }
}