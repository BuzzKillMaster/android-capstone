package com.example.littlelemoncapstone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.littlelemoncapstone.ui.theme.LittleLemonCapstoneTheme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            MenuDatabase::class.java,
            "menu.db"
        ).build()
    }

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

        lifecycleScope.launch {
            val menuItems = fetchMenu()

            runOnUiThread {
                menuItemsList.value = menuItems
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
                        ) {
                            val items by menuItemsList.observeAsState(emptyList())

                            HomeScreen()
                            items.forEach {
                                MenuItemContainer(menuItem = it)
                            }
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

@Composable
fun HomeScreen() {
    Column {
        HomeScreenHeader()
    }
}

@Composable
fun HomeScreenHeader() {
    Box(
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
    }
}

@Composable
fun MenuItemContainer(menuItem: MenuItemNetwork) {
    Column {
        Text(text = menuItem.title)
        Text(text = menuItem.description)
        Text(text = menuItem.price)
    }
}