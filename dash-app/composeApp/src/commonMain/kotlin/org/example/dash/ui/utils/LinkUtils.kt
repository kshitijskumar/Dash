package org.example.dash.ui.utils

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
    val colors = listOf(
        0xFFE57373, // Red
        0xFFBA68C8, // Purple
        0xFF64B5F6, // Blue
        0xFF4DD0E1, // Cyan
        0xFF81C784, // Green
        0xFFFFD54F, // Amber
        0xFFFF8A65, // Deep Orange
        0xFFA1887F, // Brown
        0xFF90A4AE, // Blue Grey
        0xFFF06292, // Pink
        0xFF9575CD, // Deep Purple
        0xFF4FC3F7, // Light Blue
        0xFF4DB6AC, // Teal
        0xFFAED581, // Light Green
        0xFFFFB74D, // Orange
    )
    
    val hash = name.hashCode()
    val index = (hash and Int.MAX_VALUE) % colors.size
    return colors[index]
}
