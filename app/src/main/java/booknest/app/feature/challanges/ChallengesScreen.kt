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
        StyledSectionTitle("Active Challenges")

        if (activeChallenges.isEmpty()) {
            StyledBodyText("You have no active challenges.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(activeChallenges) { challenge ->
                    ChallengeCard(
                        challenge = challenge,
                        isActive = true,
                        onStartClick = {}
                    )
                }
            }
        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.spacer_xlarge).value.dp))
        StyledSectionTitle("Available Challenges")

        if (challenges.isEmpty()) {
            StyledBodyText("No new challenges available.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(challenges) { challenge ->
                    ChallengeCard(
                        challenge = challenge,
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
    isActive: Boolean,
    onStartClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.padding_small).value.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.citric)
        ),
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
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.sand_storm),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacer_small).value.dp))

            Text(
                text = challenge.description,
                fontSize = dimensionResource(id = R.dimen.font_size_body).value.sp,
                fontFamily = FontFamily.SansSerif,
                fontStyle = FontStyle.Italic,
                color = colorResource(id = R.color.sand_storm),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small).value.dp)
            )
            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacer_small).value.dp))

            Text(
                text = "Time limit: ${challenge.timeLimit} days",
                fontSize = dimensionResource(id = R.dimen.font_size_body).value.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.sand_storm),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacer_medium).value.dp))

            if (isActive) {
                Text(
                    text = "In Progress",
                    fontSize = dimensionResource(id = R.dimen.font_size_body).value.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(id = R.color.selected),
                    textAlign = TextAlign.Center
                )
            } else {
                Button(
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.selected),
                        contentColor = colorResource(id = R.color.splash_screen_background)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Start Challenge",
                        fontWeight = FontWeight.Bold,
                        fontSize = dimensionResource(id = R.dimen.font_size_body).value.sp
                    )
                }
            }
        }
    }
}

// --- Shared Style Composables ---

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
