package booknest.app.feature.post.data

import android.location.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttractionRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AttractionRepository {

    private val attractionsCollection = firestore.collection("attractions")

    override fun fetchNearbyAttractions(
        currentLat: Double,
        currentLng: Double,
        radiusMeters: Double,
        onResult: (List<Attraction>) -> Unit
    ) {
        attractionsCollection.get()
            .addOnSuccessListener { snapshot ->
                val attractions = snapshot.documents.mapNotNull { document ->
                    val attraction = document.toObject(Attraction::class.java)
                    // Only return attractions that have coordinates
                    if (attraction != null && attraction.coordinates != null) {
                        attraction
                    } else {
                        null
                    }
                }.filter {
                    // Calculate distance using GeoPoint
                    val distance = FloatArray(1)
                    val geoPoint = it.coordinates
                    Location.distanceBetween(
                        currentLat, currentLng,
                        geoPoint!!.latitude, geoPoint.longitude,
                        distance
                    )
                    distance[0] <= radiusMeters
                }
                onResult(attractions)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
