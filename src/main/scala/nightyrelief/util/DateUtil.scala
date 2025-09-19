package nightyrelief.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateUtil:

  val DATE_PATTERN = "dd.MM.yyyy"
  val DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN)

  extension (date: LocalDate)
    // returns the given date as a well formatted String.
    def asString: String =
      if (date == null) then return null
      return DATE_FORMATTER.format(date)

  extension (dateString: String)
    // converts a String in the format of the defined
    // DATE_PATTERN to a LocalDate object.
    // returns null if the String could not be converted.
    def parseLocalDate: LocalDate =
      try
        LocalDate.parse(dateString, DATE_FORMATTER)  // dd.MM.yyyy
      catch
        case _: DateTimeParseException =>
          try
            LocalDate.parse(dateString)  // fallback to ISO (yyyy-MM-dd)
          catch
            case _: DateTimeParseException => null
    def isValid: Boolean =
      dateString.parseLocalDate != null