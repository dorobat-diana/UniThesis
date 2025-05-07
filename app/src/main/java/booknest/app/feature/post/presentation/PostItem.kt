package booknest.app.feature.post.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import booknest.app.feature.post.data.Post

@Composable
fun PostItem(
    post: Post,
    userName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = userName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        post.attraction?.let {
            Text(
                text = "📍 $it",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        post.photoUrl?.let { url ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Post photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${post.likes.size} ${if (post.likes.size == 1) "like" else "likes"}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
