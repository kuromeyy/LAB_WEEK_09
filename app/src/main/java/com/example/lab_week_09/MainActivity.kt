package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.lab_week_09.ui.theme.*
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

data class Student(
    var name: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(navController = navController)
                }
            }
        }
    }
}

@Composable
fun App(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            Home { list ->
                // Convert listData to String (as in the module example)
                val stringData = list.toString()
                val encoded = URLEncoder.encode(stringData, StandardCharsets.UTF_8.toString())
                navController.navigate("resultContent/?listData=$encoded")
            }
        }

        composable(
            "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") {
                type = NavType.StringType
            })
        ) {
            ResultContent(it.arguments?.getString("listData").orEmpty())
        }
    }
}

@Composable
fun Home(
    navigateFromHomeToResult: (List<Student>) -> Unit
) {
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }
    var inputField by remember { mutableStateOf(Student("")) }
    var showError by remember { mutableStateOf(false) }

    HomeContent(
        listData,
        inputField,
        { input ->
            inputField = inputField.copy(input)
            showError = false
        },
        {
            if (inputField.name.isBlank()) {
                showError = true
            } else {
                listData.add(inputField)
                inputField = Student("")
                showError = false
            }
        },
        { navigateFromHomeToResult(listData.toList()) },
        showError
    )
}

@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit,
    showError: Boolean
) {
    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundTitleText(text = stringResource(id = R.string.enter_item))

                TextField(
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChange = { onInputValueChange(it) },
                    isError = showError,
                    label = { Text("Enter Name") }
                )

                if (showError) {
                    Text(
                        text = "Name cannot be empty!",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row {
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_click)
                    ) {
                        onButtonClick()
                    }

                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_navigate)
                    ) {
                        navigateFromHomeToResult()
                    }
                }
            }
        }

        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

// ✅ BONUS (sesuai modul screenshot): tampilkan list sebagai String saja
@Composable
fun ResultContent(listData: String) {
    val decoded = remember {
        try {
            URLDecoder.decode(listData, StandardCharsets.UTF_8.toString())
        } catch (e: Exception) {
            listData
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        OnBackgroundItemText(text = decoded)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    Home { }
}
