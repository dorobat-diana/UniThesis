package booknest.app.feature.post.data

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import booknest.app.feature.profil.data.UserProfile
import booknest.app.feature.utils.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.await
import java.io.File

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

    override suspend fun createPost(attractionId: String, photoUri: Uri, context: Context): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val postId = UUID.randomUUID().toString()

            val inputStream = context.contentResolver.openInputStream(photoUri)
                ?: return Result.failure(Exception("Unable to open input stream from URI"))

            val requestFile = inputStream.readBytes()
                .toRequestBody("image/jpeg".toMediaTypeOrNull())

            Log.d("CreatePost", "Image size: ${requestFile.contentLength()}")

            val imagePart = MultipartBody.Part.createFormData(
                "image", // This must match what Flask expects: request.files['image']
                "photo.jpg",
                requestFile
            )
            val predictionResponse = RetrofitClient.predictApi.predictImage("predict", imagePart).await()

            try {
                val response = RetrofitClient.predictApi.predictImage("predict", imagePart).execute()
                if (response.isSuccessful) {
                    val prediction = response.body()?.attraction
                    Log.d("CreatePost", "Prediction successful: $prediction")
                } else {
                    Log.e("CreatePost", "Prediction failed with code: ${response.code()}, body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("CreatePost", "Network call failed: ${e.message}", e)
            }


            // Log the prediction result
            Log.d("CreatePost", "Prediction class: ${predictionResponse.attraction}, Attraction ID: $attractionId")

            // Compare the predicted class with the attraction's ID (or class)
            if (predictionResponse.attraction != attractionId) {
                // Log and return failure if the classes don't match
                Log.d("CreatePost", "Post creation failed: Predicted class does not match attraction ID.")
                return Result.failure(Exception("Predicted class does not match attraction ID"))
            }

            // Upload image to Firebase Storage
            val storageRef = storage.reference.child("posts/$postId.jpg")
            storageRef.putFile(photoUri).await()
            val photoUrl = storageRef.downloadUrl.await().toString()

            // Create a new Post object
            val post = Post(
                uid = postId,
                userId = userId,
                attraction = attractionId,
                photoUrl = photoUrl,
                timestamp = Timestamp(System.currentTimeMillis() / 1000, 0)
            )

            // Save the post in Firestore
            firestore.collection("posts").document(postId).set(post).await()

            // Update the user's post count in Firestore
            val userRef = firestore.collection("users").document(userId)
            userRef.update("postsCount", FieldValue.increment(1)).await()

            // Add the attraction ID to the user's visited attractions list (if not already present)
            userRef.update("visitedAttractions", FieldValue.arrayUnion(attractionId)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            // Log error if any
            Log.e("CreatePost", "Error creating post: ${e.message}", e)
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
