package triptag.app.feature.challanges.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import triptag.app.R
import triptag.app.feature.challanges.StyledBodyText
import triptag.app.feature.challanges.data.Challenge

@Composable
fun ChallengeCard(
    challenge: Challenge,
    challengeStatus: String,
    visitedAttractions: List<String> = emptyList(),
    onStartClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.6f),
                spotColor = Color.Black.copy(alpha = 0.6f)
            ),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.burn_red)),
        elevation = CardDefaults.cardElevation(0.dp)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                        containerColor = colorResource(id = R.color.citric),
                        contentColor = colorResource(id = R.color.burn_red)
                    )
                ) {
                    Text("Start Challenge", fontWeight = FontWeight.Bold)
                }
            }

            if (challengeStatus == "finished") {
                StyledBodyText(
                    "Challenge completed!",
                    color = colorResource(id = R.color.sand_storm)
                )
            }
        }
    }
}