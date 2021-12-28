data class InstituteData(
    val institute: String
) {
    val surveyResults: MutableList<SurveyResult> = mutableListOf()
}