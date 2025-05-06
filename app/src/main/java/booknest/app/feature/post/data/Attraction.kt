package booknest.app.feature.post.data

import com.google.firebase.firestore.GeoPoint

data class Attraction(
    val name: String = "",
    val coordinates: GeoPoint? = null // GeoPoint for latitude and longitude
)
