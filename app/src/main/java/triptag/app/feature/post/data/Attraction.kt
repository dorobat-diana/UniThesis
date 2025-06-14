package triptag.app.feature.post.data

import com.google.firebase.firestore.GeoPoint

data class Attraction(
    val uid: String? = null,
    val name: String = "",
    val coordinates: GeoPoint
) {
    constructor() : this(null, "", GeoPoint(0.0, 0.0))
}
