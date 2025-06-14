package triptag.app.feature.profil.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import triptag.app.R

@Composable
fun StatItem(
    count: Int,
    label: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val interactionModifier = onClick?.let { Modifier.clickable(onClick = it) } ?: Modifier
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .then(interactionModifier)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(id = R.color.sand_storm)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colorResource(id = R.color.sand_storm)
        )
    }
}
