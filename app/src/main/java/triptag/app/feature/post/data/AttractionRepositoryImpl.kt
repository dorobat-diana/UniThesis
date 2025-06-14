package triptag.app.feature.post.data

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.await
import triptag.app.feature.profil.data.UserProfile
import triptag.app.feature.utils.RetrofitClient
import java.io.ByteArrayOutputStream
import java.util.UUID
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
                    attraction
                }.filter {
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

    override suspend fun createPost(
        attractionId: String,
        photoUri: Uri,
        context: Context
    ): Result<Unit> {
        return try {
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val postId = UUID.randomUUID().toString()

            val inputStream = context.contentResolver.openInputStream(photoUri)
                ?: return Result.failure(Exception("Unable to open input stream from URI"))

            val requestFile = inputStream.readBytes()
                .toRequestBody("image/jpeg".toMediaTypeOrNull())

            val imagePart = MultipartBody.Part.createFormData(
                "image",
                "photo.jpg",
                requestFile
            )
            val predictionResponse =
                RetrofitClient.predictApi.predictImage("predict", imagePart).await()

            if (predictionResponse.attraction != attractionId) {
                return Result.failure(Exception("Predicted class does not match attraction ID"))
            }

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

            val userRef = firestore.collection("users").document(userId)
            userRef.update("postsCount", FieldValue.increment(1)).await()

            userRef.update("visitedAttractions", FieldValue.arrayUnion(attractionId)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun getVisitedAttractionsByName(attractionNames: List<String>): List<Attraction> {
        val attractions = mutableListOf<Attraction>()
        val nameChunks = attractionNames.chunked(10)

        for (chunk in nameChunks) {
            val snapshot = firestore.collection("attractions")
                .whereIn("name", chunk)
                .get()
                .await()

            for (doc in snapshot.documents) {
                val name = doc.getString("name") ?: continue
                val coords = doc.getGeoPoint("coordinates") ?: continue
                attractions.add(
                    Attraction(
                        uid = doc.id,
                        name = name,
                        coordinates = coords
                    )
                )
            }
        }
        return attractions
    }

    override suspend fun getUserProfileByUid(uid: String): UserProfile? {
        return try {
            val docSnapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            if (docSnapshot.exists()) {
                docSnapshot.toObject(UserProfile::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
