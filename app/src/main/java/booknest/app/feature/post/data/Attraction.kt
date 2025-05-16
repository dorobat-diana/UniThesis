package booknest.app.feature.post.data

import com.google.firebase.firestore.GeoPoint

data class Attraction(
    val uid: String? = null,
    val name: String = "",
    val coordinates: GeoPoint
)
