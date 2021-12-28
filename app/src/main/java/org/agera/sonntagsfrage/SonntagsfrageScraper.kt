import org.agera.sonntagsfrage.Party
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap

object SonntagsfrageScraper {
    private const val baseUrl = "https://www.wahlrecht.de"
    private const val cssSelector = "table.wilko > tbody > tr"
    private val dateFormatter = DateTimeFormatter.ofPattern("d.M.y")

    fun listSurveys(): MutableList<InstituteData> {
        return Institute.values().asList().parallelStream().map { institute -> fetchInstituteData(institute) }.collect(Collectors.toList())
    }

    private fun fetchInstituteData(institute: Institute): InstituteData {
        val result = InstituteData(institute.getLabel())
        val doc: Document

        try {
            doc = Jsoup.connect(baseUrl + institute.getPath()).get()
        } catch (e: IOException) {
            println("IO exception while fetching institute data for ${institute.getLabel()}\n" +
                    "${e.message}")
            return result
        } catch (e: HttpStatusException) {
            println("HTTP status exception while fetching institute data for ${institute.getLabel()}\n" +
                    "${e.message}")
            return result
        } catch (e: Exception) {
            println("Exception while fetching institute data for ${institute.getLabel()}\n" +
                    "${e.message}")
            return result
        }

        result.surveyResults.addAll(doc.select(cssSelector).mapNotNull { mapToSurveyResult(it, institute) }.sortedBy { it.date }.toList())
        return result
    }

    private fun mapToSurveyResult(element: Element, institute: Institute): SurveyResult? {
        val cols = element.getElementsByTag("td")

        try {
            val date = LocalDate.parse(cols[institute.getDateColumnIndex()].text(), dateFormatter)

            val result = HashMap<Party, Float>()
            result.putAll(Party.values().map { party -> Pair(party, cols[institute.getPartyColumnIndex(party)].text().toFloatCustom()) })
            return SurveyResult(date, result)
        } catch (e: Exception) {
            println("Exception while parsing survey data of institute ${institute.getLabel()}\n" +
                    "$e")
            return null
        }
    }

    private fun String.toFloatCustom(): Float {
        return this.substringBefore(" ").replace(",", ".").toFloat()
    }

}