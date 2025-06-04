package etf.ri.rma.newsfeedapp.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import etf.ri.rma.newsfeedapp.data.NewsData
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.NewsItem
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.rememberCoroutineScope
import etf.ri.rma.newsfeedapp.data.NewsService

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories =
        listOf("Sve", "Politika", "Sport", "Nauka/tehnologija", "Zdravlje", "Više filtera ...")
    val categoryToTagMap = mapOf(
        "Sve" to "filter_chip_all",
        "Politika" to "filter_chip_pol",
        "Sport" to "filter_chip_spo",
        "Nauka/" + "tehnologija" to "filter_chip_sci",
        "Zdravlje" to "filter_chip_none",
        "Više filtera ..." to "filter_chip_more"
    )

    FlowRow(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .testTag(categoryToTagMap[category] ?: "filter_chip_unknown"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun NewsFeedScreen(
    navController: NavController? = null
) {
    val newsDAO = NewsData.getNewsDAO()
    val scope = rememberCoroutineScope()

    var selectedCategory by rememberSaveable { mutableStateOf("Sve") }
    var dateRangeFilter by rememberSaveable { mutableStateOf<Pair<String, String>?>(null) }
    var unwantedWordsFilter by rememberSaveable { mutableStateOf(emptyList<String>()) }

    var isLoading by remember { mutableStateOf(false) }
    var allNews by remember { mutableStateOf<List<NewsItem>>(emptyList()) }

    val backStackEntryState = navController?.currentBackStackEntryAsState()
    val backStackEntry = backStackEntryState?.value

    LaunchedEffect(backStackEntry) {
        backStackEntry?.savedStateHandle?.remove<String>("filteredCategory")?.let {
            selectedCategory = it
        }
        backStackEntry?.savedStateHandle?.remove<Pair<String, String>?>("filteredDateRange")?.let {
            dateRangeFilter = it
        }
        backStackEntry?.savedStateHandle?.remove<List<String>>("filteredWords")?.let {
            unwantedWordsFilter = it
        }
    }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            newsDAO.ensureInitialDataLoaded()
            allNews = NewsService.getAllNews()
        } catch (e: Exception) {
            allNews = emptyList()
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(selectedCategory) {
        isLoading = true
        try{
            if (selectedCategory == "Sve"){
                allNews = NewsService.getAllNews()
            } else {
                val apiCategory = NewsService.mapAPIToUICategory(selectedCategory)
                val categoryNews = NewsService.getNewsByCategory(apiCategory, forceRefresh = true)
                allNews = categoryNews
            }
        } catch(e: Exception){
            allNews = emptyList()
        } finally {
            isLoading = false
        }
    }

    /*
    LaunchedEffect(backStackEntry?.savedStateHandle) {
        val handle = backStackEntry?.savedStateHandle ?: return@LaunchedEffect

        handle.get<String>("temporaryCategory")?.let { tempCategory ->
            handle.remove<String>("temporaryCategory")

            if (tempCategory != "Sve") {
                isLoading = true
                try {
                    val apiCategory = NewsService.mapAPIToUICategory(tempCategory)
                    val newNews = NewsService.getNewsByCategory(apiCategory, forceRefresh = true)
                    allNews = newNews + allNews
                } catch (e: Exception) {
                } finally {
                    isLoading = false
                }
            }
        }
    }

     */

    LaunchedEffect(backStackEntry?.savedStateHandle) {
        val handle = backStackEntry?.savedStateHandle ?: return@LaunchedEffect

        handle.get<String>("temporaryCategory")?.let { tempCategory ->
            handle.remove<String>("temporaryCategory")

            if (tempCategory != "Sve") {
                val clickedCategories = handle.get<List<String>>("clickedCategories") ?: emptyList()

                isLoading = true
                try { val allNewNews = mutableListOf<NewsItem>()

                    for (category in clickedCategories) {
                        if (category != "Sve") {
                            val apiCategory = NewsService.mapAPIToUICategory(category)
                            val newNews = NewsService.getNewsByCategory(apiCategory, forceRefresh = true)
                            allNewNews.addAll(newNews)
                        }
                    }

                    // Kombinovati sa postojećim vestima i ukloniti duplikate
                    //val uniqueNews = (allNewNews + allNews).distinctBy { it.uuid } // ili neki drugi unique identifikator
                    allNews = allNewNews + allNews

                } catch (e: Exception) {
                } finally {
                    isLoading = false
                }
            }
        }
    }

    val filteredNews = remember(selectedCategory, dateRangeFilter, unwantedWordsFilter) {
        allNews.filter { news ->
            (selectedCategory == "Sve" || news.category == selectedCategory) && (dateRangeFilter?.let { range ->
                val newsDate = parseDate(news.publishedDate)
                val start = parseDate(range.first)
                val end = parseDate(range.second)
                newsDate in start..end
            } ?: true) &&
            unwantedWordsFilter.none { word ->
                news.title.contains(word, ignoreCase = true) || news.snippet.contains(word, ignoreCase = true)
            }
        }
    }

    val displayNews = remember(allNews, dateRangeFilter, unwantedWordsFilter) {
        allNews.filter { news ->
            (dateRangeFilter?.let { range ->
                val newsDate = parseDate(news.publishedDate)
                val start = parseDate(range.first)
                val end = parseDate(range.second)
                newsDate in start..end
            } ?: true) &&
                    unwantedWordsFilter.none { word ->
                        news.title.contains(word, ignoreCase = true) ||
                                news.snippet.contains(word, ignoreCase = true)
                    }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FilterSection(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    if (category == "Više filtera ...") {
                        navController?.navigate("filters")
                    } else {
                        selectedCategory = category
                    }
                },
                modifier = Modifier.padding(8.dp)
            )

            NewsList(
                newsItems = displayNews,
                selectedCategory = selectedCategory,
                onNewsSelected = { newsId -> navController?.navigate("details/$newsId") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("news_list")
            )
        }
    }
}

private fun mapCategoryToApi(category: String): String {
    return when (category) {
        "Politika" -> "politics"
        "Sport" -> "sports"
        "Nauka/tehnologija" -> "tech"
        "Zdravlje" -> "health"
        else -> "general"
    }
}

@Composable
fun NewsList(
    newsItems: List<NewsItem>,
    selectedCategory: String,
    onNewsSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.testTag("news_list"),
        state = rememberLazyListState()
    ) {
        if (newsItems.isEmpty()) {
            item {
                MessageCard(message = "Nema pronađenih vijesti u kategoriji $selectedCategory")
            }
        } else {
            items(newsItems) { newsItem ->
                if (newsItem.isFeatured) {
                    FeaturedNewsCard(
                        newsItem = newsItem,
                        onClick = { onNewsSelected(newsItem.uuid) }
                    )
                } else {
                    StandardNewsCard(
                        newsItem = newsItem,
                        onClick = { onNewsSelected(newsItem.uuid) })
                }
            }
        }
    }
}