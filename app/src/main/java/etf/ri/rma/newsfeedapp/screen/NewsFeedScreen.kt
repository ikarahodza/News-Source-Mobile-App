package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.NewsItem
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.FlowRow

@Composable
fun NewsFeedScreen() {
    val allNews = NewsData.getAllNews()
    var selectedCategory by remember { mutableStateOf("Sve") }

    val filteredNews = when (selectedCategory) {
        "Sve" -> allNews
        else -> allNews.filter { it.category == selectedCategory }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FilterSection(
                selectedCategory = selectedCategory,
                onCategorySelected = { category -> selectedCategory = category },
                modifier = Modifier.padding(8.dp)
            )

            NewsList(
                newsItems = filteredNews,
                selectedCategory = selectedCategory,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("Sve", "Politika", "Sport", "Nauka/tehnologija", "Umjetnost")
    val categoryToTagMap = mapOf(
        "Sve" to "filter_chip_all",
        "Politika" to "filter_chip_pol",
        "Sport" to "filter_chip_spo",
        "Nauka/tehnologija" to "filter_chip_sci",
        "Umjetnost" to "filter_chip_none"
    )

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
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
fun NewsList(
    newsItems: List<NewsItem>,
    selectedCategory: String,
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
                    FeaturedNewsCard(newsItem = newsItem)
                } else {
                    StandardNewsCard(newsItem = newsItem)
                }
            }
        }
    }
}

