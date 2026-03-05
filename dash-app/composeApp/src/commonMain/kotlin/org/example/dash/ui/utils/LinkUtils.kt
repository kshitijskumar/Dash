package org.example.dash.ui.utils

import org.example.dash.ui.theme.AppColors

fun extractInitials(name: String): String {
    val trimmedName = name.trim()
    val words = trimmedName.split(Regex("\\s+")).filter { it.isNotEmpty() }
    
    return when {
        words.size >= 2 -> {
            val firstChar = words[0].firstOrNull()?.uppercaseChar()
            val secondChar = words[1].firstOrNull()?.uppercaseChar()
            if (firstChar != null && secondChar != null && 
                firstChar.isLetter() && secondChar.isLetter()) {
                "$firstChar$secondChar"
            } else {
                extractFromSingleWord(words[0])
            }
        }
        words.size == 1 -> extractFromSingleWord(words[0])
        else -> "??"
    }
}

private fun extractFromSingleWord(word: String): String {
    val alphabetOnly = word.filter { it.isLetter() }
    return when {
        alphabetOnly.length >= 2 -> {
            "${alphabetOnly.first().uppercaseChar()}${alphabetOnly.last().uppercaseChar()}"
        }
        alphabetOnly.length == 1 -> {
            "${alphabetOnly.first().uppercaseChar()}${alphabetOnly.first().uppercaseChar()}"
        }
        else -> "??"
    }
}

fun getColorForName(name: String): Long {
    val hash = name.hashCode()
    val index = (hash and Int.MAX_VALUE) % AppColors.LinkColors.size
    return AppColors.LinkColors[index]
}
