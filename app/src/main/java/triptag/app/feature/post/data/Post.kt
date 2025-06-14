package triptag.app.feature.post.data

import com.google.firebase.Timestamp

data class Post(
    val uid: String? = null,
    val userId: String? = null,
    val attraction: String? = null,
    val photoUrl: String? = null,
    val timestamp: Timestamp? = null
)
