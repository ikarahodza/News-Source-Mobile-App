package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import etf.ri.rma.newsfeedapp.R
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.util.Log

@Composable
fun StandardNewsCard(
    newsItem: NewsItem,
    onClick: () -> Unit = {}
) {
    val date = newsItem.publishedDate
    val fixed = formatDate(date)

    val context = LocalContext.current

    val imageUrl = newsItem.imageUrl
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .error(R.drawable.newsimg)
            .placeholder(R.drawable.newsimg)
            .build()
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Log.d("StandardNewsCard", "Rendering: ${newsItem.imageUrl}")
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .placeholder(R.drawable.newsimg)
                    .error(R.drawable.newsimg)
                    .crossfade(true)
                    .build(),
                contentDescription = "News image",
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = newsItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = newsItem.snippet,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${newsItem.source} • ${fixed}",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    fun formatDate(isoDate: String): String {
        return try {
            val instant = java.time.Instant.parse(isoDate)
            val date = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            val formatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale("bs"))
            " ${date.format(formatter)}"
        } catch (e: Exception) {
            "Datum: $isoDate"
        }
    }
}

