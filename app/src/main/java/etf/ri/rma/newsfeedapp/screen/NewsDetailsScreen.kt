package etf.ri.rma.newsfeedapp.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import etf.ri.rma.newsfeedapp.data.NewsData
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import kotlin.math.abs


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
            .filter { it.category == currentNews.category }
            .sortedBy { it.title }
            .sortedBy { news ->
                val currentDate = LocalDate.parse(currentNews.publishedDate, formatter)
                val newsDate = LocalDate.parse(news.publishedDate, formatter)
                abs(ChronoUnit.DAYS.between(currentDate, newsDate))
            }
            .filter { it.id != currentNews.id }
            .take(2)
    }

    BackHandler(onBack = onClose)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
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

fun parseDate(dateString: String): Date {
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return format.parse(dateString) ?: Date()
}