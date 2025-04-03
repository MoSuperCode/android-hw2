package com.example.hw2_if23b071

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hw2_if23b071.dto.MagicCard
import com.example.hw2_if23b071.network.NetworkUtils
import com.example.hw2_if23b071.parser.MagicCardParser
import com.example.hw2_if23b071.ui.theme.Hw2if23b071Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    // hier speichere ich die momentane page
    private var currentPage = 1

    companion object {
        private const val STATE_PAGE = "current_page"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // kann damit die page number wiederherstellen, wenn sie noch existiert
        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(STATE_PAGE,1)
        }

        enableEdgeToEdge()
        setContent {
            Hw2if23b071Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MagicCardListScreen(
                        initialPage = currentPage,
                        onPageChange = { page -> currentPage = page},
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // hier speichen wir die pagenumber, damit wir sie oben wiederherstellen können
        outState.putInt(STATE_PAGE,currentPage)
        super.onSaveInstanceState(outState)
    }
}

@Composable
fun MagicCardListScreen(
    initialPage:Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var cardListText by rememberSaveable { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(true) }
    var currentPage by rememberSaveable { mutableStateOf(initialPage) }

    val coroutineScope = rememberCoroutineScope()
    val networkUtils = remember { NetworkUtils } // TODO DEBUG
    val cardParser = remember { MagicCardParser()}
    // muss man so machen sonst kriegt man ein fehler -> Lehrer Fragen (Error: @Composable invocations can only happen from the context of a @Composable function)
    val loadingText = stringResource(R.string.loading)
    val errorText = stringResource(R.string.error_message)

    Column(modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Das ist meine scrollbare textarea
        Text(
            text = cardListText,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        )

        Button(
            onClick = {
                // TODO: card loading logic
                isButtonEnabled = false
                cardListText = loadingText

                coroutineScope.launch {
                    try {
                        // fetchen auf dem input autput thread
                        val jsonData = withContext(Dispatchers.IO) {
                            networkUtils.fetchCardData(currentPage)
                        }

                        if (jsonData != null) {
                            // parsen auf dem default thread
                            val cards = withContext(Dispatchers.Default) {
                                cardParser.parseJson(jsonData)
                            }
                            // wenn die liste leer ist, sind wir bei der letzen page angekommen, also dann müssma wieder von neu beginnen
                            if (cards.isEmpty()) {
                                currentPage = 1
                                onPageChange(currentPage)
                                //damit wir wieder die erste page fetchen
                                isButtonEnabled = true
                                // sobald nochmal auf den button gedrückt wird, lädt jz die erste page wieder
                                return@launch
                            }
                            // karten nach name alphabetisch sortieren
                            val sortedCards = cards.sortedBy { it.name }

                            // formatiert und zeigt karten an
                            cardListText = formatCardList(sortedCards)

                            // incrementier der page für die nächste fetchung brudaa
                            currentPage++
                            onPageChange(currentPage)
                        } else {
                            cardListText = errorText
                        }

                    } catch (e: Exception) {
                        Log.e("MagicCardList", "Error loading cards", e)
                        cardListText = errorText
                    } finally {
                        isButtonEnabled = true
                    }
                }



                // nach dem laden wieder Enablen
                },
                enabled = isButtonEnabled
        ) {
            Text(text = stringResource(R.string.load_cards))
        }

    }

}
private fun formatCardList(cards: List<MagicCard>): String {
    val builder = StringBuilder()

    for (card in cards) {
        builder.append("${card.name}: ${card.type}, ${card.rarity}, ${card.colors.joinToString(", ")}\n")
    }

    return builder.toString()
}

