package booknest.app.feature.challanges

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import booknest.app.R
import booknest.app.feature.challanges.presentation.ChallengesViewModel
import booknest.app.feature.challanges.presentation.ChallengeCard

@Composable
fun ChallengesScreen(
    userId: String,
    viewModel: ChallengesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val challenges by viewModel.challenges.collectAsState()
    val activeChallenges by viewModel.activeChallenges.collectAsState()
    val finishedChallenges by viewModel.finishedChallenges.collectAsState()

    val navBackStackEntry = LocalLifecycleOwner.current
    val lifecycle = navBackStackEntry.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshChallenges(userId)
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(userId) {
        viewModel.refreshChallenges(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium).value.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StyledHeader("Challenges")
        Spacer(Modifier.height(dimensionResource(id = R.dimen.spacer_large).value.dp))

        // --- Active Challenges ---
        StyledSectionTitle("Active Challenges")
        if (activeChallenges.isEmpty()) {
            StyledBodyText("You have no active challenges.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(activeChallenges) { challenge ->
                    val userChallenge = viewModel.getUserChallenge(userId, challenge.id)

                    ChallengeCard(
                        challenge = challenge,
                        challengeStatus = "active",
                        visitedAttractions = userChallenge?.attractionsFound ?: emptyList()
                    )
                }
            }
        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.spacer_xlarge).value.dp))

        // --- Available Challenges ---
        StyledSectionTitle("Available Challenges")
        if (challenges.isEmpty()) {
            StyledBodyText("No new challenges available.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(challenges) { challenge ->
                    ChallengeCard(
                        challenge = challenge,
                        challengeStatus = "available"
                    ) {
                        viewModel.startChallenge(userId, challenge.id)
                    }
                }
            }
        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.spacer_xlarge).value.dp))

        // --- Finished Challenges ---
        StyledSectionTitle("Finished Challenges")
        if (finishedChallenges.isEmpty()) {
            StyledBodyText("You haven't completed any challenges yet.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(finishedChallenges) { finished ->
                    ChallengeCard(
                        challenge = finished,
                        challengeStatus = "finished"
                    )
                }
            }
        }
    }
}

@Composable
fun StyledHeader(text: String) {
    Text(
        text = text,
        fontSize = dimensionResource(id = R.dimen.font_size_header).value.sp,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.ExtraBold,
        color = colorResource(id = R.color.sand_storm),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun StyledSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = dimensionResource(id = R.dimen.font_size_section_title).value.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        color = colorResource(id = R.color.sand_storm),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(dimensionResource(id = R.dimen.spacer_medium).value.dp))
}

@Composable
fun StyledBodyText(text: String, color: Color = colorResource(id = R.color.sand_storm)) {
    Text(
        text = text,
        fontSize = dimensionResource(id = R.dimen.font_size_body).value.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
