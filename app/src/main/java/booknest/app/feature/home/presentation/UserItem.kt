package booknest.app.feature.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import booknest.app.R
import coil.compose.rememberAsyncImagePainter
import booknest.app.feature.profil.data.UserProfile

@Composable
fun UserItem(user: UserProfile, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(user.profilePictureUrl),
            contentDescription = "${user.username}'s profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = user.username ?: "Unknown", style = MaterialTheme.typography.titleMedium, color = colorResource(id = R.color.sand_storm))
            if (user.caption.isNotBlank()) {
                Text(text = user.caption, style = MaterialTheme.typography.bodySmall, color = colorResource(id = R.color.sand_storm))
            }
        }
    }
}
