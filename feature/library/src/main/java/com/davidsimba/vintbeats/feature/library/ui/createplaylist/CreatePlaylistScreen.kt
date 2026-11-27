package com.davidsimba.vintbeats.feature.library.ui.createplaylist

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.davidsimba.vintbeats.core.common.VintCropImageContract
import com.davidsimba.vintbeats.core.common.VintCropImageContractOptions
import com.davidsimba.vintbeats.feature.library.R
import java.io.File

@Composable
fun CreatePlaylistScreen(
    onBack: () -> Unit,
    onCreated: (Int) -> Unit,
    viewModel: CreatePlaylistViewModel = hiltViewModel(),
) {
    val cropImage = rememberLauncherForActivityResult(VintCropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { viewModel.onImagePicked(it) }
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                cropImage.launch(
                    VintCropImageContractOptions(
                        uri = it,
                        cropImageOptions = CropImageOptions(
                            aspectRatioX = 1,
                            aspectRatioY = 1,
                            fixAspectRatio = true,
                            guidelines = CropImageView.Guidelines.ON,
                            activityBackgroundColor = 0xFF121212.toInt(),
                            toolbarColor = 0xFF121212.toInt(),
                            toolbarTitleColor = 0xFFFFFFFF.toInt(),
                            toolbarBackButtonColor = 0xFFFFFFFF.toInt(),
                            borderLineColor = 0xFFDD7733.toInt(),
                            borderCornerColor = 0xFFDD7733.toInt(),
                            guidelinesColor = 0x44DD7733,
                        ),
                    )
                )
            }
        },
    )

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val name = viewModel.name
    val canSave = name.isNotBlank()
    val hasCover = viewModel.coverImagePath != null
    val nameLabel = stringResource(R.string.playlist_name_label)
    val coverDescription = stringResource(R.string.playlist_cover_description)
    val dashColor = Color(0xFF3F3F46)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E10))
            .statusBarsPadding()
            .imePadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 8.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.playlist_cancel),
                    tint = Color(0xFFF4F4F5),
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(if (viewModel.isEditMode) R.string.edit_playlist_title else R.string.create_playlist_title),
                color = Color(0xFFF4F4F5),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 28.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val pickCover = {
                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF16161A))
                    .then(
                        if (hasCover) Modifier else Modifier.drawBehind {
                            drawRoundRect(
                                color = dashColor,
                                cornerRadius = CornerRadius(14.dp.toPx()),
                                style = Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 8.dp.toPx())),
                                ),
                            )
                        }
                    )
                    .clickable(role = Role.Button, onClickLabel = coverDescription) { pickCover() }
                    .semantics { contentDescription = coverDescription },
                contentAlignment = Alignment.Center,
            ) {
                if (hasCover) {
                    AsyncImage(
                        model = File(viewModel.coverImagePath!!),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.playlist_cover_change),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF27272A)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                contentDescription = null,
                                tint = Color(0xFFE4E4E7),
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.playlist_cover_add),
                            color = Color(0xFFE4E4E7),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(R.string.playlist_cover_optional),
                            color = Color(0xFFA1A1AA),
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = nameLabel,
                    color = Color(0xFFD4D4D8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                BasicTextField(
                    value = name,
                    onValueChange = { viewModel.onNameChange(it.take(NAME_MAX_LENGTH)) },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color(0xFFF4F4F5),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { if (canSave) viewModel.save(onCreated) }),
                    decorationBox = { inner ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1C1C1F))
                                .border(
                                    1.5.dp,
                                    if (name.isEmpty()) Color(0xFF27272A) else Color(0xFFF4F4F5),
                                    RoundedCornerShape(12.dp),
                                )
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (name.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.playlist_name_hint),
                                    color = Color(0xFF71717A),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            inner()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .semantics { contentDescription = nameLabel },
                )
                Text(
                    text = "${name.length}/$NAME_MAX_LENGTH",
                    color = Color(0xFF71717A),
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                )
            }

            Spacer(Modifier.height(28.dp))

            val buttonColor by animateColorAsState(
                if (canSave) Color(0xFFF4F4F5) else Color(0xFF27272A),
                tween(200),
                label = "create_button",
            )
            val buttonText by animateColorAsState(
                if (canSave) Color(0xFF0E0E10) else Color(0xFFA1A1AA),
                tween(200),
                label = "create_button_text",
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(buttonColor)
                    .clickable(enabled = canSave, role = Role.Button) { viewModel.save(onCreated) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(if (viewModel.isEditMode) R.string.save_playlist_action else R.string.library_create_playlist),
                    color = buttonText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            if (!viewModel.isEditMode) {
                Text(
                    text = stringResource(R.string.playlist_add_songs_later),
                    color = Color(0xFF71717A),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}

private const val NAME_MAX_LENGTH = 50
