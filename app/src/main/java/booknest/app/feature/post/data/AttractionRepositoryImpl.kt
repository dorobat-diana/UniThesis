package booknest.app.feature.post.data

import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import com.google.firebase.Timestamp

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

    suspend fun uploadPostImage(bitmap: Bitmap, postId: String): String {
        val storageRef = storage.reference.child("posts/$postId.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        storageRef.putBytes(data).await()
        return storageRef.downloadUrl.await().toString()
    }

    override suspend fun createPost(attractionId: String, photoUri: Uri): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val postId = UUID.randomUUID().toString()

            val storageRef = storage.reference.child("posts/$postId.jpg")
            storageRef.putFile(photoUri).await()
            val photoUrl = storageRef.downloadUrl.await().toString()

            val post = Post(
                uid = postId,
                userId = userId,
                attraction = attractionId,
                photoUrl = photoUrl,
                timestamp = Timestamp(System.currentTimeMillis() / 1000, 0)
            )

            firestore.collection("posts").document(postId).set(post).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
