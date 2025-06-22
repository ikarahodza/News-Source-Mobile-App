package etf.ri.rma.newsfeedapp.screen

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import etf.ri.rma.newsfeedapp.model.NewsItem
import coil.compose.AsyncImage
import etf.ri.rma.newsfeedapp.data.NewsDatabase
import etf.ri.rma.newsfeedapp.data.NewsService
import etf.ri.rma.newsfeedapp.data.NewsService.allNews
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.toDomain


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsDetailsScreen(
    newsId: String,
    onClose: () -> Unit,
    onRelatedNewsSelected: (String) -> Unit
) {
    var currentNews by remember { mutableStateOf<NewsItem?>(null) }
    val scrollState = rememberScrollState()
    val imagaDAO = remember { ImagaDAO() }
    var imageTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingTags by remember { mutableStateOf(false) }
    var isLoading by remember {mutableStateOf(true)}
    var error by remember { mutableStateOf<String?>(null) }
    var relatedNews by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    val fetchedSimilarFor = remember { mutableStateMapOf<String, Boolean>() }

    val context = LocalContext.current
    val savedNewsDao = NewsDatabase.getInstance(context).savedNewsDAO()

    val isOnline = remember {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    suspend fun fetchNewsById(uuid: String): NewsItem? {
        return if(isOnline) {
            try {
                NewsService.getNewsById(uuid)
            } catch(e:Exception) {
                savedNewsDao.getNewsById(uuid)?.toDomain()
            }
        } else {
            savedNewsDao.getNewsById(uuid)?.toDomain()
        }
    }

    suspend fun fetchSimilarNews(newsItem: NewsItem): List<NewsItem> {
        return if (isOnline) {
            try {
                val similar = NewsService.getSimilarStories(newsItem.uuid)

                similar.forEach { item ->
                    try {
                        val saved = savedNewsDao.saveNews(item)
                        if(saved && item.imageTags.isNotEmpty()) {
                            savedNewsDao.getNewsById(item.uuid)?.id?.let {
                                id -> savedNewsDao.addTags(item.imageTags, id)
                            }
                        }
                    } catch(e: Exception) {

                    }
                }
                similar
            } catch(e:Exception) {
                savedNewsDao.getNewsWithCategory(newsItem.category).filter { it.uuid != newsItem.uuid}
            }
        } else {
            savedNewsDao.getNewsWithCategory(newsItem.category).filter { it.uuid != newsItem.uuid }
        }
    }

    suspend fun fetchImageTags(newsItem: NewsItem): List<String> {
        Log.d("NewsDetails", "Fetchanje tagova")
        val newsExists = savedNewsDao.saveNews(newsItem)
        val dbId = savedNewsDao.getNewsIdByUuid(newsItem.uuid)

        val cached = dbId?.let { savedNewsDao.getTags(it)} ?: emptyList()

        if(cached.isNotEmpty()) {
            return cached
        }

        if(isOnline && !newsItem.imageUrl.isNullOrEmpty()) {
            return try {
                val response = imagaDAO.getImageTags(newsItem.imageUrl)
                val tags = response.result.tags.map {it.tag.en}

                if(tags.isNotEmpty() && dbId != null) {
                    savedNewsDao.addTags(tags, dbId)
                }
                Log.d("NewsDetails", "Fetchani tagovi")
                tags
            } catch (e:Exception) {
                emptyList()
            }
        }
        return emptyList()
    }

    LaunchedEffect(newsId) {
        isLoading = true
        try {
            //val newsItem = NewsService.getNewsById(newsId)

            val newsItem = fetchNewsById(newsId)
            if (newsItem == null) {
                return@LaunchedEffect
            }
            currentNews = newsItem

            try {
                Log.d("NewsDetailsScreen", "Pokrenut LaunchedEffect za $newsId")
                if (fetchedSimilarFor[newsId] != true) {
                    try {
                        //relatedNews = NewsService.getSimilarStories(newsId)
                        val similar = fetchSimilarNews(newsItem)
                        relatedNews = similar.take(2)
                        fetchedSimilarFor[newsId] = true

                        val updatedList = allNews.value.toMutableList()
                        relatedNews.forEach { item ->
                            if (updatedList.none { it.uuid == item.uuid }) {
                                updatedList.add(item.copy(isFeatured = false))
                            }
                        }
                    } catch (e: Exception) {
                        relatedNews = emptyList()
                        fetchedSimilarFor[newsId] = true
                    }
                }

                val updatedList = allNews.value.toMutableList()
                relatedNews.forEach { item ->
                    if (updatedList.none { it.uuid == item.uuid }) {
                        updatedList.add(item.copy(isFeatured = false))
                    }
                }
            } catch (e: Exception) {
                relatedNews = emptyList()
            }

            isLoadingTags = true
            try {
                /*
                newsItem.imageUrl?.let { url ->
                    val response = imagaDAO.getImageTags(url)
                    Log.d("NewsDetailsScreen", "Rendering: ${newsItem.imageTags}")
                    imageTags = response.result.tags.map { it.tag.en }
                }
                 */
                savedNewsDao.saveNews(newsItem)

                val tags = fetchImageTags(newsItem)
                imageTags = tags
            } catch (e: Exception) {
                imageTags = emptyList()
            }
            isLoadingTags = false

        } catch (e: Exception){
        } finally {
            isLoading = false
        }
    }

    BackHandler(onBack = onClose)

    when {
        error != null -> {
            Box(modifier = Modifier.fillMaxSize()){
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error ?: "Nepoznata greška",
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = onClose) {
                        Text("Nazad")
                    }
                }
            }
        }

        currentNews != null -> {
            val news = currentNews!!

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)
            ) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.testTag("detail-title").padding(top = 16.dp)
                )

                currentNews!!.imageUrl?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "News image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = news.snippet,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp).testTag("details-snippet")
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.fillMaxWidth()){
                    Text(
                        text = "Kategorija: ${news.category}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp).testTag("details_category")
                    )
                    Text(
                        text = "Izvor: ${news.source}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp).testTag("details_source")
                    )
                    Text(
                        text = "Objavljeno: ${formatDate(news.publishedDate)}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp).testTag("details_date")
                    )
                }

                if (imageTags.isNotEmpty()) {
                    Text(
                        text = "Tagovi slike:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        imageTags.forEach { tag ->
                            SuggestionChip(
                                onClick = { },
                                label = { Text(tag) },
                                modifier = Modifier.testTag("image_tag_$tag")
                            )
                        }
                    }
                } else if (!isLoadingTags) {
                    Text(
                        text = "Nema dostupnih tagova",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Povezane vijesti iz iste kategorije",
                    style = MaterialTheme.typography.titleMedium
                )

                if (relatedNews.isNotEmpty()) {
                    relatedNews.getOrNull(0)?.let {
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRelatedNewsSelected(it.uuid) }
                                .padding(vertical = 8.dp)
                                .testTag("related_news_title_1")
                        )
                    }

                    relatedNews.getOrNull(1)?.let {
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRelatedNewsSelected(it.uuid) }
                                .padding(vertical = 8.dp)
                                .testTag("related_news_title_2")
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onClose,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("details_close_button")
                    ) {
                        Text(text = "Zatvori detalje")
                    }
                }
            }
        }
        else -> {
            Text("Nepoznata greška")
        }
    }
}



fun parseDate(dateString: String): Date {
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return format.parse(dateString) ?: Date()
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




