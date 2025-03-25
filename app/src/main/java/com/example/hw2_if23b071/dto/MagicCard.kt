package com.example.hw2_if23b071.dto

data class MagicCard(
    val name: String ="",
    val type: String ="",
    val rarity: String="",
    val colors: List<String> = emptyList()
)
