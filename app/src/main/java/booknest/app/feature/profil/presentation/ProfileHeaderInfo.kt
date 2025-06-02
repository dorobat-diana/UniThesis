package booknest.app.feature.profil.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import booknest.app.feature.profil.data.UserProfile
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter

@Composable
fun ProfileHeaderInfo(
    profile: UserProfile, // Your existing UserProfile data class
    isOwnProfile: Boolean,
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    editedUsername: String,
    onUsernameChange: (String) -> Unit,
    editedCaption: String,
    onCaptionChange: (String) -> Unit,
    onUpdateProfile: () -> Unit,
    onProfilePicChangeLaunch: () -> Unit,
    isFriend: Boolean?,
    currentUserUid: String?,
    targetUid: String?, // This is the 'uid' of the profile being viewed
    onAddFriend: (currentUid: String, targetUid: String) -> Unit,
    onRemoveFriend: (currentUid: String, targetUid: String) -> Unit,
    navController: NavHostController,
    sandStormColor: Color, // Pass color resource for convenience
    citricColor: Color // Pass color resource
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top // Align items to the top of the row
    ) {
        // Left: Profile Picture
        Box(
            modifier = Modifier
                .size(96.dp) // Adjusted size for side-by-side layout
                .clip(CircleShape)
                .clickable(enabled = isOwnProfile) {
                    if (isOwnProfile) {
                        onProfilePicChangeLaunch()
                    }
                }
        ) {
            Image(
                painter = rememberAsyncImagePainter(profile.profilePictureUrl),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // "Edit" text overlay on picture removed for cleaner, Instagram-like look
        }

        Spacer(modifier = Modifier.width(16.dp)) // Space between picture and info

        // Right: Info Column (Stats, Name, Bio, Button)
        Column(modifier = Modifier.weight(1f)) { // weight(1f) makes it take available horizontal space
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(count = profile.postsCount, label = "Posts")
                StatItem(
                    count = profile.friendsCount,
                    label = "Friends",
                    onClick = { navController.navigate("friends_screen/${profile.uid}") }
                )
                StatItem(count = profile.completedChallenges, label = "Challenges")
                StatItem(count = profile.level, label = "Level")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Name (Username)
            if (isEditing && isOwnProfile) {
                OutlinedTextField(
                    value = editedUsername,
                    onValueChange = onUsernameChange,
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = sandStormColor),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = sandStormColor,
                        unfocusedTextColor = sandStormColor,
                        cursorColor = sandStormColor,
                        focusedLabelColor = sandStormColor,
                        unfocusedLabelColor = sandStormColor,
                        focusedBorderColor = sandStormColor,
                        unfocusedBorderColor = sandStormColor.copy(alpha = 0.7f)
                    )
                )
            } else {
                Text(
                    text = profile.username ?: "No username",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = sandStormColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Description (Bio/Caption)
            if (isEditing && isOwnProfile) {
                OutlinedTextField(
                    value = editedCaption,
                    onValueChange = onCaptionChange,
                    label = { Text("Bio") }, // Changed "Caption" to "Bio"
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4, // Allow multi-line bio
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = sandStormColor),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = sandStormColor,
                        unfocusedTextColor = sandStormColor,
                        cursorColor = sandStormColor,
                        focusedLabelColor = sandStormColor,
                        unfocusedLabelColor = sandStormColor,
                        focusedBorderColor = sandStormColor,
                        unfocusedBorderColor = sandStormColor.copy(alpha = 0.7f)
                    )
                )
            } else {
                if (!profile.caption.isNullOrEmpty()) {
                    Text(
                        text = profile.caption,
                        style = MaterialTheme.typography.bodyMedium,
                        color = sandStormColor,
                        maxLines = 4
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button: Edit Profile / Save / Add Friend / Unfriend
            if (isOwnProfile) {
                Button(
                    onClick = {
                        if (isEditing) {
                            onUpdateProfile() // Call the update function
                        }
                        onEditToggle() // Toggle editing state
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = citricColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isEditing) "Save Profile" else "Edit Profile")
                }
            } else if (targetUid != null && currentUserUid != null) { // For other users' profiles
                Button(
                    onClick = {
                        if (isFriend == true) {
                            onRemoveFriend(currentUserUid, targetUid)
                        } else {
                            onAddFriend(currentUserUid, targetUid)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = citricColor,
                        contentColor = Color.White
                    ),
                    enabled = isFriend != null // Disable button while friend status is loading
                ) {
                    Text(
                        text = when (isFriend) {
                            true -> "Unfriend"
                            false -> "Add Friend"
                            null -> "Loading..." // Placeholder while checking friend status
                        },
                        style = MaterialTheme.typography.bodyMedium.copy( // Adjusted style
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}