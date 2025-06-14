package triptag.app.feature.post.presentation

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import triptag.app.R
import triptag.app.feature.post.data.Post

@Composable
fun PostItem(
    post: Post,
    userName: String,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    onLikeClick: () -> Unit,
    isLiked: Boolean,
    likeCount: Int
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = userName,
                color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontStyle = FontStyle.Italic
                )
            )
        }

        post.photoUrl?.let { url ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Post image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(onClick = onLikeClick) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Like",
                    tint = if (isLiked) Color(
                        ContextCompat.getColor(
                            context,
                            R.color.selected
                        )
                    ) else Color(ContextCompat.getColor(context, R.color.unselected))
                )
            }
        }

        Text(
            text = "$likeCount ${if (likeCount == 1) "like" else "likes"}",
            color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontStyle = FontStyle.Italic
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        post.attraction?.let {
            Text(
                text = "$userName 📍 $it",
                color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontStyle = FontStyle.Italic
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}
