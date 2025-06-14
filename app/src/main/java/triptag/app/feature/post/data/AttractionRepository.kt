package triptag.app.feature.post.data

import android.content.Context
import android.net.Uri
import triptag.app.feature.profil.data.UserProfile

interface AttractionRepository {
    fun fetchNearbyAttractions(
        currentLat: Double,
        currentLng: Double,
        radiusMeters: Double = 500.0,
        onResult: (List<Attraction>) -> Unit
    )

    suspend fun createPost(string: String, photoUri: Uri, context: Context): Result<Unit>
    suspend fun getVisitedAttractionsByName(attractionNames: List<String>): List<Attraction>
    suspend fun getUserProfileByUid(uid: String): UserProfile?
}
