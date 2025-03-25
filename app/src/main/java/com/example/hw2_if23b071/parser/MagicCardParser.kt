package com.example.hw2_if23b071.parser

import android.util.Log
import com.example.hw2_if23b071.dto.MagicCard
import org.json.JSONObject

class MagicCardParser {
    private val TAG = "MagicCardParser"

    fun parseJson(jsonString:String): List<MagicCard> {
        val cards = mutableListOf<MagicCard>()

        try {
            val root = JSONObject(jsonString)
            val cardsArray = root.optJSONArray("cards") ?: return emptyList()

            for (i in 0 until cardsArray.length()) {
                val cardObject = cardsArray.getJSONObject(i)

                val name = cardObject.optString("name", "<unnamed>")
                val type = cardObject.optString("type", "<untyped>")
                val rarity = cardObject.optString("rarity", "<unranked>")

                val colorsArray = cardObject.optJSONArray("colors")
                val colors = mutableListOf<String>()
                if (colorsArray != null) {
                    for (j in 0 until colorsArray.length()) {
                        colors.add(colorsArray.getString(j))
                    }
                }
                cards.add(MagicCard(
                    name = name,
                    type = type,
                    rarity = rarity,
                    colors = colors
                ))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON",e)
        }
        return cards
    }

}