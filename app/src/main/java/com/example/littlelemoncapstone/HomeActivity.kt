package com.example.littlelemoncapstone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.littlelemoncapstone.ui.theme.LittleLemonCapstoneTheme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class HomeActivity : ComponentActivity() {
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            MenuDatabase::class.java,
            "menu.db"
        ).fallbackToDestructiveMigration().build()
    }

    // Create a CoroutineScope
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    // register ktor
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(contentType = ContentType("text", "plain"))
        }
    }

    private val menuItemsList = MutableLiveData<List<MenuItemNetwork>>()


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences("LittleLemon", MODE_PRIVATE)
        val firstLaunch = preferences.getBoolean("firstLaunch", true)

        if (firstLaunch) {
            coroutineScope.launch {
                val menuItems = fetchMenu()

                menuItems.forEach() {
                    val menuItem = MenuItem(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        price = it.price,
                        image = it.image,
                        category = it.category
                    )

                    database.menuDao().insert(menuItem)
                }

                preferences.edit().putBoolean("firstLaunch", false).apply()
            }
        }

        setContent {
            LittleLemonCapstoneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Little Lemon") },
                                actions = {
                                    IconButton(onClick = { launchProfileActivity() }) {
                                        Icon(Icons.Filled.Person, contentDescription = "Profile")
                                    }
                                }
                            )
                        }
                    ) { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .verticalScroll(rememberScrollState())
                        ) {
                            val menuItems by database.menuDao().getAll().observeAsState(emptyList())

                            val options = listOf("Starters", "Mains", "Desserts")
                            val selectedOption = remember { mutableStateOf<String?>(null) }
                            val searchTerm = remember { mutableStateOf("") }

                            val sortedMenuItems = menuItems.filter {
                                it.title.lowercase().contains(searchTerm.value.lowercase())
                                        && (selectedOption.value == null || it.category == selectedOption.value!!.lowercase())
                            }

                            HomeScreenHeader(searchTerm = searchTerm)
                            HomeScreenSortingOptions(options = options, selectedOption = selectedOption)
                            HomeScreenMenu(menuItems = sortedMenuItems)
                        }
                    }
                }
            }
        }
    }

    private fun launchProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private suspend fun fetchMenu(): List<MenuItemNetwork> {
        val response: MenuNetwork =
            client.get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/menu.json")
                .body()

        return response.menu
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenHeader(searchTerm: MutableState<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF495E57))
            .padding(16.dp)
    ) {
        Row {
            Column(
                modifier = Modifier.weight(0.8f)
            ) {
                Text(
                    text = "Little Lemon",
                    color = Color(0xFFF4CE14),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Chicago",
                    color = Color(0xFFEDEFEE),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                Text(
                    text = "We are a family owned Mediterranean restaurant, focused on traditional recipes served with a modern twist.",
                    color = Color(0xFFEDEFEE),
                )
            }

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.weight(0.2f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = searchTerm.value,
            onValueChange = { searchTerm.value = it },
            placeholder = { Text("Enter Search Phrase") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun HomeScreenSortingOptions(options: List<String>, selectedOption: MutableState<String?>) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        selectedOption.value = if (selectedOption.value == option) null else option
                    }
            ) {
                SortingOption(text = option, selected = option == selectedOption.value)
            }
        }
    }
}

@Composable
fun SortingOption(text: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFFF4CE14) else Color(0xFF495E57))
            .padding(16.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color(0xFF495E57) else Color(0xFFF4CE14),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun HomeScreenMenu(menuItems: List<MenuItem>) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        menuItems.forEach {
            MenuItemContainer(menuItem = it)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MenuItemContainer(menuItem: MenuItem) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.75f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = menuItem.title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )

            Text(
                text = menuItem.description,
                modifier = Modifier.padding(vertical = 8.dp),
            )

            Text(
                text = "$${menuItem.price}",
                fontWeight = FontWeight.Bold,
            )
        }

        Box(modifier = Modifier
            .weight(0.25f)
            .aspectRatio(1f)
        ) {
            GlideImage(
                model = menuItem.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}