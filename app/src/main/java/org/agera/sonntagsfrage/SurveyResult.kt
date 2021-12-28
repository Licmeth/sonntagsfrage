import org.agera.sonntagsfrage.Party
import java.time.LocalDate
import java.util.*
import kotlin.collections.HashMap

data class SurveyResult(
    val date: LocalDate,
    val result: HashMap<Party, Float>
)