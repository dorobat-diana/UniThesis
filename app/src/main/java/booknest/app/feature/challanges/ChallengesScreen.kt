package booknest.app.feature.challanges

import android.content.Context
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
fun ChallengeCard(
    challenge: Challenge,
    challengeStatus: String, // "active", "available", "finished"
    visitedAttractions: List<String> = emptyList(),
    onStartClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.padding_small).value.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.citric)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium).value.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = challenge.title,
                fontSize = dimensionResource(id = R.dimen.font_size_title).value.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.sand_storm),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = challenge.description,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                color = colorResource(id = R.color.sand_storm),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Time limit: ${challenge.timeLimit} days",
                fontSize = 13.sp,
                color = colorResource(id = R.color.sand_storm)
            )

            Spacer(Modifier.height(12.dp))

            if (challengeStatus == "active") {
                StyledBodyText("Checklist:", color = colorResource(id = R.color.sand_storm))
                challenge.attractionsToFind.forEach { attraction ->
                    val found = visitedAttractions.contains(attraction)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = found, onCheckedChange = null, enabled = false)
                        Text(
                            text = attraction,
                            fontWeight = if (found) FontWeight.Bold else FontWeight.Normal,
                            color = colorResource(id = R.color.sand_storm)
                        )
                    }
                }
            }

            if (challengeStatus == "available") {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.selected),
                        contentColor = colorResource(id = R.color.splash_screen_background)
                    )
                ) {
                    Text("Start Challenge", fontWeight = FontWeight.Bold)
                }
            }

            if (challengeStatus == "finished") {
                StyledBodyText("✅ Challenge completed!", color = colorResource(id = R.color.sand_storm))
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
        fontStyle = FontStyle.Italic,
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
        fontStyle = FontStyle.Italic,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(dimensionResource(id = R.dimen.spacer_medium).value.dp))
}

@Composable
fun StyledBodyText(text: String, color: Color = colorResource(id = R.color.white)) {
    Text(
        text = text,
        fontSize = dimensionResource(id = R.dimen.font_size_body).value.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
