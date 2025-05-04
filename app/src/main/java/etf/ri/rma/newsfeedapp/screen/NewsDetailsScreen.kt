package etf.ri.rma.newsfeedapp.screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import etf.ri.rma.newsfeedapp.data.NewsData
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/*
@Composable
fun NewsDetailsScreen (
    newsId: String,
    onBack: () -> Unit,
    onNewsSelected: (String) -> Unit
) {
    val newsItem = remember(newsId) { NewsData.getNewsById(newsId)}
    val relatedNews = remember (newsItem) {
        NewsData.getAllNews()
            .filter{ it.category == newsItem.category && it.id  != newsItem.id }
            .sortedWith(compareBy(
                { Math.abs(daysBetween(it.publishedDate, newsItem.publishedDate)) },
                { it.title }
            ))
            .take(2)
    }

    Scaffold{ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.testTag("details_title")
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = newsItem.snippet,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("details_snippet")
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kategorija: ${newsItem.category}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("details_category")
            )
            Text(
                text = "Izvor: ${newsItem.source}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("details_source")
            )
            Text(
                text = "Datum: ${newsItem.publishedDate}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("details_date")
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Povezane vijesti iz iste kategorije: ",
                style = MaterialTheme.typography.titleMedium
            )
            relatedNews.forEachIndexed { index, news ->
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable { onNewsSelected(news.id) }
                        .testTag("related_news_title_${index + 1}")
                )

            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(16.dp)
                .testTag("details_close_button")
        ) {
            Text("Zatvori detalje")
        }
    }
}

 */

@Composable
fun NewsDetailsScreen(
    newsId: String,
    onClose: () -> Unit,
    onRelatedNewsSelected: (String) -> Unit
) {
    val allNews = NewsData.getAllNews()
    val currentNews = allNews.firstOrNull { it.id == newsId } ?: return
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    val relatedNews = remember(currentNews) {
        allNews
            .filter { it.category == currentNews.category } // Include current news in the pool first
            .sortedBy { it.title } // Primary sort by title
            .sortedBy { news ->
                val currentDate = LocalDate.parse(currentNews.publishedDate, formatter)
                val newsDate = LocalDate.parse(news.publishedDate, formatter)
                abs(ChronoUnit.DAYS.between(currentDate, newsDate))
            }
            .filter { it.id != currentNews.id } // Remove current news after sorting
            .take(2) // Take the two closest
    }

    BackHandler(onBack = onClose)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Current news details
        Text(
            text = currentNews.title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(16.dp)
                .testTag("details_title")
        )
        Text(
            text = currentNews.snippet,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(16.dp)
                .testTag("details_snippet")
        )
        Text(
            text = "Kategorija: ${currentNews.category}",
            modifier = Modifier
                .padding(16.dp)
                .testTag("details_category")
        )
        Text(
            text = "Izvor: ${currentNews.source}",
            modifier = Modifier
                .padding(16.dp)
                .testTag("details_source")
        )
        Text(
            text = "Datum: ${currentNews.publishedDate}",
            modifier = Modifier
                .padding(16.dp)
                .testTag("details_date")
        )

        // Related news section
        Text(
            text = "Povezane vijesti iz iste kategorije:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        relatedNews.forEachIndexed { index, news ->
            Text(
                text = news.title,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onRelatedNewsSelected(news.id) }
                    .testTag("related_news_title_${index + 1}"),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        Button(
            onClick = onClose,
            modifier = Modifier
                .padding(16.dp)
                .testTag("details_close_button")
        ) {
            Text("Zatvori detalje")
        }
    }
}

fun daysBetween(dateStr1: String, dateStr2: String): Long {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    try {
        val date1 = LocalDate.parse(dateStr1, formatter)
        val date2 = LocalDate.parse(dateStr2, formatter)
        return abs(ChronoUnit.DAYS.between(date1, date2))
    } catch (e: Exception) {
        return 0L
    }
}

fun parseDate(dateString: String): Date {
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return format.parse(dateString) ?: Date()
}