package com.example.autocompletesearch

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.autocompletesearch.ui.theme.AutoCompleteSearchTheme
import com.example.autocompletesearch.ui.theme.Purple500
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoCompleteSearchTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = { TopBar() },
                        backgroundColor = Purple500
                    ) {
                        CountryNavigation()
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Country List",
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        backgroundColor = Purple500,
        contentColor = Color.White
    )
}

@Composable
fun CountryNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "CountryList"
    ) {
        composable("CountryList") {
            CountryListScreen()
        }
    }
}

@Composable
fun CountryListScreen() {
    val textValue = remember { mutableStateOf(TextFieldValue("")) }
    Column {
        SearchCountryList(textValue)
        CountryList(textValue)
    }
}

@Composable
fun SearchCountryList(textValue: MutableState<TextFieldValue>) {
    TextField(
        value = textValue.value,
        onValueChange = { textValue.value = it },
        placeholder = { Text(text = "Search Country Name") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Icon",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (textValue.value != TextFieldValue("")) {
                IconButton(onClick = {
                    textValue.value = TextFieldValue("")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            cursorColor = Color.Black,
            trailingIconColor = Color.Black,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
fun CountryList(textValue: MutableState<TextFieldValue>) {
    val context = LocalContext.current
    val countries = getListOfCountries()
    var filteredCountries: ArrayList<String>
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        val searchText = textValue.value.text
        filteredCountries = if (searchText.isEmpty()) {
            countries
        } else {
            val resultList = ArrayList<String>()
            for (country in countries) {
                if (country.lowercase(Locale.getDefault())
                        .contains(searchText.lowercase(Locale.getDefault()))
                ) {
                    resultList.add(country)
                }
            }
            resultList
        }
        items(filteredCountries) { filteredCountries ->
            CountryListItem(
                countryText = filteredCountries,
                onItemClick = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun CountryListItem(
    countryText: String,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onItemClick(countryText)
            }
            .background(Color.White)
            .height(60.dp)
            .fillMaxWidth()
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = countryText,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
fun getListOfCountries(): ArrayList<String> {
    val isoCountryCodes = Locale.getISOCountries()
    val countryListWithEmojis = ArrayList<String>()

    for (countryCode in isoCountryCodes) {
        val locale = Locale("", countryCode)
        val countryName = locale.displayCountry
        val flagOffset = 0x1F1E6
        val asciOffset = 0x41
        val firstChar = Character.codePointAt(countryCode, 0) - asciOffset + flagOffset
        val secondChar = Character.codePointAt(countryCode, 1) - asciOffset + flagOffset
        val flag = (String(Character.toChars(firstChar)) + String(Character.toChars(secondChar)))
        countryListWithEmojis.add("$countryName (${locale.country}) $flag")
    }
    return countryListWithEmojis
}
