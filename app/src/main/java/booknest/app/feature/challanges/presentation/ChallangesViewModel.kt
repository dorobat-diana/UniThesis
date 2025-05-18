package booknest.app.feature.challanges.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import booknest.app.feature.challanges.data.Challenge
import booknest.app.feature.challanges.data.ChallengeRepository
import booknest.app.feature.challanges.data.UserChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val repository: ChallengeRepository
) : ViewModel() {

    private val _challenges = MutableStateFlow<List<Challenge>>(emptyList())
    val challenges: StateFlow<List<Challenge>> = _challenges

    private val _activeChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val activeChallenges: StateFlow<List<Challenge>> = _activeChallenges

    private val _finishedChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val finishedChallenges: StateFlow<List<Challenge>> = _finishedChallenges

    private val _userChallenges = MutableStateFlow<List<UserChallenge>>(emptyList())
    val userChallenges: StateFlow<List<UserChallenge>> = _userChallenges


    fun loadChallenges(userId: String) {
        viewModelScope.launch {
            val available = repository.getAvailableChallengesForUser(userId)
            val active = repository.getUserActiveChallenges(userId)
            val finished = repository.getUserFinishedChallenges(userId)
            val userSpecific = repository.getUserChallenges(userId)

            _challenges.value = available
            _activeChallenges.value = active
            _finishedChallenges.value = finished
            _userChallenges.value = userSpecific
        }
    }


    fun startChallenge(userId: String, challengeId: String) {
        viewModelScope.launch {
            repository.startChallengeForUser(userId, challengeId.toString())
            loadChallenges(userId)
        }
    }

    fun refreshChallenges(userId: String) {
        viewModelScope.launch {
            repository.checkAndTerminateExpiredChallenges(userId)
            loadChallenges(userId)
        }
    }

    fun getUserChallenge(userId: String, challengeId: String): UserChallenge? {
        return userChallenges.value.find {
            it.userId == userId && it.activeChallengeId == challengeId
        }
    }


}