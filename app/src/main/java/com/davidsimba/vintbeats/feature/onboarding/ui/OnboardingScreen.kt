package com.davidsimba.vintbeats.feature.onboarding.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.davidsimba.vintbeats.core.util.VintCropImageContract
import com.davidsimba.vintbeats.core.util.VintCropImageContractOptions
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.feature.search.ui.components.SearchField
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import java.io.File

@Composable
fun OnboardingScreen(
    onDone: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val step by viewModel.step.collectAsStateWithLifecycle()

    BackHandler(enabled = step == 1) { viewModel.goBack() }

    AnimatedContent(
        targetState = step,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }
        },
        label = "onboarding_step"
    ) { currentStep ->
        when (currentStep) {
            0 -> NameStep(viewModel = viewModel)
            else -> ArtistsStep(viewModel = viewModel, onDone = onDone)
        }
    }
}

@Composable
private fun NameStep(viewModel: OnboardingViewModel) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val photoPath by viewModel.photoPath.collectAsStateWithLifecycle()

    val cropImage = rememberLauncherForActivityResult(VintCropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { viewModel.onPhotoSelected(it) }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VintageBgDark)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(VintageGrayDeep)
                .clickable {
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            if (photoPath != null) {
                AsyncImage(
                    model = File(photoPath!!),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt,
                    contentDescription = null,
                    tint = VintageGrayMid,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
        Text(
            text = stringResource(R.string.onboarding_add_photo),
            color = VintageGrayDeep,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 28.dp),
        )

        Text(
            text = stringResource(R.string.onboarding_welcome),
            color = VintageWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.onboarding_name_question),
            color = VintageGrayMid,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 24.dp),
        )
        OutlinedTextField(
            value = name,
            onValueChange = viewModel::onNameChange,
            placeholder = { Text(stringResource(R.string.onboarding_name_hint), color = VintageGrayDeep) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VintageWhite,
                unfocusedBorderColor = VintageGrayDeep,
                focusedTextColor = VintageWhiteWarm,
                unfocusedTextColor = VintageWhiteWarm,
                cursorColor = VintageWhite,
                focusedContainerColor = VintageBgDark,
                unfocusedContainerColor = VintageBgDark,
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { viewModel.goToArtists() }),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = viewModel::goToArtists,
            enabled = name.isNotBlank(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VintageWhite,
                contentColor = VintageBgDark,
                disabledContainerColor = VintageGrayDeep.copy(alpha = 0.3f),
                disabledContentColor = VintageGrayMid,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(text = stringResource(R.string.onboarding_continue), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun ArtistsStep(viewModel: OnboardingViewModel, onDone: () -> Unit) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val selected by viewModel.selected.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VintageBgDark)
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, end = 20.dp, top = 8.dp)
        ) {
            IconButton(onClick = viewModel::goBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = VintageWhite,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.onboarding_pick_artists),
                    color = VintageWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = stringResource(R.string.onboarding_pick_artists_subtitle),
                    color = VintageGrayMid,
                    fontSize = 13.sp,
                )
            }
        }

        if (selected.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 12.dp)
            ) {
                selected.forEach { artist ->
                    SelectedArtistChip(artist = artist, onRemove = { viewModel.toggleArtist(artist) })
                }
            }
        } else {
            Spacer(Modifier.height(16.dp))
        }

        SearchField(
            query = query,
            onQueryChange = viewModel::onQueryChange,
            autoFocus = false,
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = VintageWhite,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(results) { artist ->
                        ArtistRow(
                            artist = artist,
                            isSelected = viewModel.isSelected(artist.id),
                            onClick = { viewModel.toggleArtist(artist) }
                        )
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.complete(onDone) },
            enabled = selected.size >= 3,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VintageWhite,
                contentColor = VintageBgDark,
                disabledContainerColor = VintageGrayDeep.copy(alpha = 0.3f),
                disabledContentColor = VintageGrayMid,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .height(52.dp)
        ) {
            val label = if (selected.size < 3)
                stringResource(R.string.onboarding_select_more, 3 - selected.size)
            else
                stringResource(R.string.onboarding_done_count, selected.size)
            Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun SelectedArtistChip(artist: Artist, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(VintageWhite.copy(alpha = 0.1f))
            .padding(start = 6.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AsyncImage(
            model = artist.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(24.dp).clip(CircleShape)
        )
        Text(text = artist.name, color = VintageWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null,
            tint = VintageGrayMid,
            modifier = Modifier.size(14.dp).clickable { onRemove() }
        )
    }
}

@Composable
private fun ArtistRow(artist: Artist, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(VintageGrayDeep)
        ) {
            AsyncImage(
                model = artist.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = artist.name,
            color = VintageWhiteWarm,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = VintageWhite,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
