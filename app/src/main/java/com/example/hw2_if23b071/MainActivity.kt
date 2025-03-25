package com.example.hw2_if23b071

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hw2_if23b071.ui.theme.Hw2if23b071Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hw2if23b071Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MagicCardListScreen(

                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MagicCardListScreen( modifier: Modifier = Modifier) {
    var cardListText by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(true) }

    Column(modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Das ist meine scrollbare textarea
        Text(
            text = "cardListText",
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        )

        Button(
            onClick = {
                // TODO: card loading logic
                isButtonEnabled = false
                cardListText = "Loading..."
                // nach dem laden wieder Enablen
                isButtonEnabled = true
                },
                enabled = isButtonEnabled
        ) {
            Text(text = stringResource(R.string.load_cards))
        }

    }

}

