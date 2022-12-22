package fr.ftnl.grupo.core.utils

import com.github.sisyphsu.dateparser.DateParser
import java.time.format.DateTimeParseException
import java.util.*

class DateTimeUtils {
    fun parseFromString(input: String): Date? {
        val string = input.lowercase().replace("h", ":").replace("m", ":")
        
        val parser: DateParser = DateParser.newBuilder().build()
        var result: Date? = null
        try {
            result = parser.parseDate(string)
        } catch (e: DateTimeParseException) { /*ignored*/
        }
        if (string.startsWith("<")) {
            try {
                val dateMilis = string.replace("((?!\\d).)*".toRegex(), "")
                var dateLong = dateMilis.toLong()
                if (dateLong < 10000000000L) dateLong *= 1000
                result = Date(dateLong)
            } catch (ignored: NumberFormatException) { /*ignored*/
            }
        }
        return result
    }
}