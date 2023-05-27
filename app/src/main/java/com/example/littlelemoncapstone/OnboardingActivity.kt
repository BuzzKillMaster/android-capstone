package com.example.littlelemoncapstone

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.littlelemoncapstone.ui.theme.LittleLemonCapstoneTheme
import kotlinx.coroutines.launch

class OnboardingActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LittleLemonCapstoneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Onboarding") },
                                navigationIcon = {
                                    IconButton(onClick = { finish() }) {
                                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                                    }
                                }
                            )
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                        ) {
                            OnboardingScreen(registerUser = { name, email ->
                                registerUser(name, email)
                            })
                        }
                    }
                }
            }
        }
    }

    private fun registerUser(name: String, email: String) {
        val preferences = getSharedPreferences("user", MODE_PRIVATE)

        // TODO: Save name and email to preferences using a User object and Gson

        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(registerUser: (String, String) -> Unit = { _, _ -> }) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    val focus = LocalFocusManager.current

    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }

    fun backButtonEnabled(): Boolean {
        return when (pagerState.currentPage) {
            0 -> false
            else -> true
        }
    }

    fun nextButtonEnabled(): Boolean {
        return when (pagerState.currentPage) {
            0 -> name.value.isNotEmpty()
            1 -> email.value.isNotEmpty() && email.value.matches(Patterns.EMAIL_ADDRESS.toRegex())
            else -> true
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        HorizontalPager(
            pageCount = 3,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> PageView {
                    Text(
                        text = "Let's get to know you a little better!\nWhat should we call you?",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        label = { Text("Your name") },
                        value = name.value,
                        onValueChange = { name.value = it },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                1 -> PageView {
                    Text(
                        "It's nice to meet you, ${name.value}.\nHow may we contact you?",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        label = { Text("Your email") },
                        value = email.value,
                        onValueChange = { email.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                2 -> PageView {
                    Text(
                        text = "Thank you for joining Little Lemon.\nWelcome to the community!",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    focus.clearFocus()

                    scope.launch {
                        pagerState.scrollToPage(pagerState.currentPage - 1)
                    }
                },
                enabled = backButtonEnabled(),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(text = "Back")
            }

            Button(
                onClick = {
                    scope.launch {
                        focus.clearFocus()

                        if (pagerState.canScrollForward) {
                            pagerState.scrollToPage(pagerState.currentPage + 1)
                        } else {
                            registerUser(name.value, email.value)
                        }
                    }
                },
                enabled = nextButtonEnabled(),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(text = if (pagerState.canScrollForward) "Next" else "Complete")
            }
        }
    }
}

@Composable
fun PageView(children: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        children()
    }
}