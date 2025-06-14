package triptag.app.feature.challanges.data

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val attractionsToFind: List<String>,
    val timeLimit: Int
) {
    constructor() : this("","","",emptyList<String>(),0)
}
