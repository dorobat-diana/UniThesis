package booknest.app.feature.post.data

interface AttractionRepository {
    fun fetchNearbyAttractions(
        currentLat: Double,
        currentLng: Double,
        radiusMeters: Double = 500.0,
        onResult: (List<Attraction>) -> Unit
    )
}
