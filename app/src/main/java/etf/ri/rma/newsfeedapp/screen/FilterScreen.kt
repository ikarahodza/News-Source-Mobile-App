package etf.ri.rma.newsfeedapp.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDateRangePickerState
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavController
) {
    val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle


    var selectedCategory by remember {
        mutableStateOf(
            savedStateHandle?.get<String>("selectedCategory") ?: "Sve"
        )
    }

    var clickedCategories by remember {
        mutableStateOf(
            savedStateHandle?.get<List<String>>("clickedCategories") ?: emptySet()
        )
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()
    var dateRangeDisplay by remember {
        mutableStateOf(
            savedStateHandle?.get<Pair<String, String>?>("dateRange")?.let {
                "${it.first} - ${it.second}"
            } ?: "Odaberite opseg datuma"
        )
    }
    var unwantedWordInput by remember { mutableStateOf(TextFieldValue("")) }
    var unwantedWords by remember {
        mutableStateOf(
            savedStateHandle?.get<List<String>>("unwantedWords") ?: emptyList()
        )
    }
    var newWord by remember { mutableStateOf("") }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Filtriraj po kategoriji:", style = MaterialTheme.typography.titleMedium)

            FilterSection(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    if (!clickedCategories.contains(category)) {
                        clickedCategories = clickedCategories + category
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text("Filtriraj po datumu:", style = MaterialTheme.typography.titleMedium)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = dateRangeDisplay,
                    modifier = Modifier.weight(1f).testTag("filter_daterange_display")
                )

                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.testTag("filter_daterange_button")
                ) {
                    Text("Odaberi")
                }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            val start = dateRangePickerState.selectedStartDateMillis
                            val end = dateRangePickerState.selectedEndDateMillis
                            if (start != null && end != null) {
                                val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                dateRangeDisplay =
                                    "${format.format(Date(start))};${format.format(Date(end))}"
                            }
                        }) {
                            Text("Potvrdi")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Odustani")
                        }
                    }
                ) {
                    DateRangePicker(state = dateRangePickerState)
                }
            }

            Text("Filtriraj nepoželjne riječi:", style = MaterialTheme.typography.titleMedium)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                TextField(
                    value = newWord,
                    onValueChange = { newWord = it },
                    label = { Text("Unesite nepoželjnu riječ") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("filter_unwanted_input"),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (newWord.isNotBlank()) {
                            unwantedWords = unwantedWords + newWord
                            newWord = ""
                        }
                    },
                    modifier = Modifier.testTag("filter_unwanted_add_button")
                ) {
                    Text("Dodaj")
                }
            }

            if (unwantedWords.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .testTag("filter_unwanted_list")
                ) {
                    Text("Nepoželjne riječi:")
                    unwantedWords.forEach {
                        Text(text = it)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))


                Button(
                    onClick = {
                        // Poslati trenutno selektovanu kategoriju
                        navController.previousBackStackEntry?.savedStateHandle?.set("temporaryCategory", selectedCategory)
                        // Poslati i set kliknanih kategorija
                        navController.previousBackStackEntry?.savedStateHandle?.set("clickedCategories", clickedCategories)
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Nazad")
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val range =
                            if (dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null) {
                                val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                val start =
                                    format.format(Date(dateRangePickerState.selectedStartDateMillis!!))
                                val end =
                                    format.format(Date(dateRangePickerState.selectedEndDateMillis!!))
                                Pair(start, end)
                            } else null

                        navController.previousBackStackEntry?.savedStateHandle?.apply {
                            set("filteredCategory", selectedCategory)
                            set("filteredDateRange", range)
                            set("filteredWords", unwantedWords)
                            set("clickedCategories", clickedCategories)
                        }

                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("filter_apply_button")
                ) {
                    Text("Primijeni filtere")
                }
            }
        }
    }
