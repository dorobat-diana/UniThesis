package booknest.app.feature.profil.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import booknest.app.feature.profil.data.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    fun loadUser(uid: String) {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "Loading user profile for UID: $uid")
                val doc = firestore.collection("users").document(uid).get().await()
                val userProfile = doc.toObject(UserProfile::class.java)
                Log.d("ProfileViewModel", "Fetched user: $userProfile")
                _profile.value = userProfile
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to load user profile: ${e.message}", e)
            }
        }
    }
}