package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.util.UUID

object NewsData {

    private val initialNews = listOf(
        NewsItem(
            uuid = UUID.randomUUID().toString(),
            title = "Breakthrough in Quantum Computing Technology",
            snippet = "Scientists at leading research institutions have achieved a major milestone in quantum computing, demonstrating unprecedented processing capabilities that could revolutionize multiple industries.",
            imageUrl = "https://example.com/quantum-computing.jpg",
            publishedDate = "01-06-2025",
            category = "Nauka/tehnologija",
            source = "Tech News Daily",
            isFeatured = false
        ),
        NewsItem(
            uuid = UUID.randomUUID().toString(),
            title = "Global Climate Summit Reaches Historic Agreement",
            snippet = "World leaders gathered at the annual climate summit have reached a groundbreaking agreement on carbon emission reductions, setting ambitious targets for the next decade.",
            imageUrl = "https://example.com/climate-summit.jpg",
            publishedDate = "01-06-2025",
            category = "Politika",
            source = "Environmental Report",
            isFeatured = false
        ),
        NewsItem(
            uuid = UUID.randomUUID().toString(),
            title = "Championship Final Breaks Viewership Records",
            snippet = "The championship final attracted over 100 million viewers worldwide, setting new records for sports broadcasting and digital streaming platforms.",
            imageUrl = "https://example.com/championship.jpg",
            publishedDate = "31-05-2025",
            category = "Sport",
            source = "Sports Network",
            isFeatured = false
        ),
        NewsItem(
            uuid = UUID.randomUUID().toString(),
            title = "Revolutionary Art Exhibition Opens in Major Gallery",
            snippet = "A groundbreaking art exhibition featuring works from emerging artists opens this week, showcasing innovative techniques and contemporary themes.",
            imageUrl = "https://example.com/art-exhibition.jpg",
            publishedDate = "31-05-2025",
            category = "Umjetnost",
            source = "Art Today",
            isFeatured = false
        ),
        NewsItem(
            uuid = UUID.randomUUID().toString(),
            title = "Medical Research Breakthrough in Cancer Treatment",
            snippet = "Researchers have developed a new cancer treatment that shows remarkable success rates in clinical trials, offering hope to millions of patients worldwide.",
            imageUrl = "https://example.com/medical-research.jpg",
            publishedDate = "30-05-2025",
            category = "Nauka/tehnologija",
            source = "Medical Journal",
            isFeatured = false
        ),
        NewsItem(
            uuid = UUID.randomUUID().toString(),
            title = "Major Political Reform Proposal Announced",
            snippet = "Government officials have announced a comprehensive political reform proposal aimed at increasing transparency and citizen participation in democratic processes.",
            imageUrl = "https://example.com/political-reform.jpg",
            publishedDate = "30-05-2025",
            category = "Politika",
            source = "Political Times",
            isFeatured = false
        ),
        NewsItem(
            uuid = UUID.randomUUID().toString(),
            title = "Olympic Preparations Reach Final Stage",
            snippet = "With months to go before the games, Olympic preparations are in full swing as athletes from around the world make final adjustments to their training regimens.",
            imageUrl = "https://example.com/olympics.jpg",
            publishedDate = "29-05-2025",
            category = "Sport",
            source = "Olympic News",
            isFeatured = false
        ),
        NewsItem(
            uuid = UUID.randomUUID().toString(),
            title = "Digital Art Revolution Transforms Creative Industry",
            snippet = "Digital art platforms and NFT marketplaces are reshaping how artists create, distribute, and monetize their work in the modern creative economy.",
            imageUrl = "https://example.com/digital-art.jpg",
            publishedDate = "29-05-2025",
            category = "Umjetnost",
            source = "Digital Arts Weekly",
            isFeatured = false
        ),
        NewsItem(
            uuid = UUID.randomUUID().toString(),
            title = "Space Exploration Mission Achieves New Milestone",
            snippet = "The latest space exploration mission has successfully achieved its primary objectives, bringing scientists closer to understanding the mysteries of our solar system.",
            imageUrl = "https://example.com/space-mission.jpg",
            publishedDate = "28-05-2025",
            category = "Nauka/tehnologija",
            source = "Space Science Today",
            isFeatured = false
        ),
        NewsItem(
            uuid = UUID.randomUUID().toString(),
            title = "International Trade Agreement Finalised",
            snippet = "Major world economies have finalized a comprehensive trade agreement that promises to boost global commerce and economic cooperation.",
            imageUrl = "https://example.com/trade-agreement.jpg",
            publishedDate = "28-05-2025",
            category = "Politika",
            source = "Economic Herald",
            isFeatured = false
        )
    )

    private val allNews: MutableList<NewsItem> = initialNews.toMutableList()

    private var newsDAO: NewsDAO? = null
    private var imageDAO: ImagaDAO? = null

    fun getAllNews(): List<NewsItem> {
        return allNews.toList()
    }

    fun addNews(news: List<NewsItem>) {
        news.forEach { newItem ->
            val existingIndex = allNews.indexOfFirst { it.uuid == newItem.uuid }
            if (existingIndex != -1) {
                allNews[existingIndex] = newItem
            } else {
                allNews.add(0, newItem)
            }
        }
    }

    fun updateNewsItem(newsItem: NewsItem) {
        val index = allNews.indexOfFirst { it.uuid == newsItem.uuid }
        if (index != -1) {
            allNews[index] = newsItem
        }
    }

    fun getNewsDAO(): NewsDAO {
        return newsDAO ?: run {
            val dao = NewsDAO()
            newsDAO = dao
            dao
        }
    }

    fun getImageDAO(): ImagaDAO {
        return imageDAO ?: run {
            val dao = ImagaDAO()
            imageDAO = dao
            dao
        }
    }
}

