package booknest.app.feature.challanges

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import booknest.app.R
import booknest.app.feature.challanges.data.Challenge
import booknest.app.feature.challanges.presentation.ChallengesViewModel

@Composable
fun ChallengesScreen(
    userId: String,
    viewModel: ChallengesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val challenges by viewModel.challenges.collectAsState()
    val activeChallenges by viewModel.activeChallenges.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadChallenges(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StyledHeader("Challenges", context)

        Spacer(Modifier.height(24.dp))
        StyledSectionTitle("Active Challenges", context)

        if (activeChallenges.isEmpty()) {
            StyledBodyText("You have no active challenges.", context)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(activeChallenges) { challenge ->
                    ChallengeCard(
                        challenge = challenge,
                        context = context,
                        isActive = true,
                        onStartClick = {}
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        StyledSectionTitle("Available Challenges", context)

        if (challenges.isEmpty()) {
            StyledBodyText("No new challenges available.", context)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(challenges) { challenge ->
                    ChallengeCard(
                        challenge = challenge,
                        context = context,
                        isActive = false
                    ) {
                        viewModel.startChallenge(userId, challenge.id)
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: Challenge,
    context: Context,
    isActive: Boolean,
    onStartClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.citric)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StyledTitleText(challenge.title, context)
            Spacer(Modifier.height(4.dp))
            StyledBodyText(challenge.description, context)
            Spacer(Modifier.height(4.dp))
            StyledBodyText("Time limit: ${challenge.timeLimit} days", context)
            Spacer(Modifier.height(8.dp))

            if (isActive) {
                StyledBodyText("In Progress", context, color = Color(colorResource(id = R.color.selected).value.toLong()))
            } else {
                Button(
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(colorResource(id = R.color.selected).value.toLong())
                    )
                ) {
                    Text("Start Challenge")
                }
            }
        }
    }
}

// --- Shared Style Composables ---

@Composable
fun StyledHeader(text: String, context: Context) {
    Text(
        text = text,
        fontSize = 28.sp,
        fontFamily = FontFamily.SansSerif,
        style = MaterialTheme.typography.headlineMedium.copy(
            color = Color.White,
            fontStyle = FontStyle.Italic
        ),
        textAlign = TextAlign.Center,
        color = Color(ContextCompat.getColor(context, R.color.sand_storm))
    )
}

@Composable
fun StyledSectionTitle(text: String, context: Context) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontFamily = FontFamily.SansSerif,
        style = MaterialTheme.typography.titleLarge.copy(
            color = Color.White,
            fontStyle = FontStyle.Italic
        ),
        textAlign = TextAlign.Center,
        color = Color(ContextCompat.getColor(context, R.color.sand_storm))
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
fun StyledTitleText(text: String, context: Context) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontFamily = FontFamily.SansSerif,
        style = MaterialTheme.typography.titleLarge.copy(
            color = Color.White,
            fontStyle = FontStyle.Italic
        ),
        color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
        textAlign = TextAlign.Center
    )
}

@Composable
fun StyledBodyText(text: String, context: Context, color: Color = Color.White) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontFamily = FontFamily.SansSerif,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = color,
            fontStyle = FontStyle.Italic
        ),
        color = color,
        textAlign = TextAlign.Center
    )
}
